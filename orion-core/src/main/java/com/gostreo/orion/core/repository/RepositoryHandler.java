package com.gostreo.orion.core.repository;

import com.gostreo.orion.api.Orchestrator;
import com.gostreo.orion.api.jobs.OrionPromise;
import com.gostreo.orion.api.proto.OrionMessageProto;
import com.gostreo.orion.api.repository.OrionRepository;
import com.gostreo.orion.api.repository.annotations.Channel;
import com.gostreo.orion.core.adapters.ReactorPromiseAdapter;
import com.gostreo.orion.core.mesh.DistributedJobTracker;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

public class RepositoryHandler<Parent extends Orchestrator, Provider extends OrionRepository<Parent>>
        implements InvocationHandler {

    private static final String BREAKER_INSTANCE = "orion-cluster-break";

    private final CircuitBreaker circuitBreaker = CircuitBreaker.of(BREAKER_INSTANCE,
            CircuitBreakerConfig.ofDefaults());

    private final String targetChannel;
    private final DistributedJobTracker distributedJobTracker;
    private final GenericRepositoryProvider<Parent, Provider> repositoryProvider;

    public RepositoryHandler(DistributedJobTracker distributedJobTracker,
                             GenericRepositoryProvider<Parent, Provider> repositoryProvider) {

        this.distributedJobTracker = distributedJobTracker;
        this.repositoryProvider = repositoryProvider;

        Channel channelAnn = repositoryProvider.getClassInterface().getAnnotation(Channel.class);
        this.targetChannel = (channelAnn != null) ? channelAnn.channelName() : "default-grid";
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Generical Call
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }

        UUID jobId = UUID.randomUUID();

        OrionMessageProto message = new OrionMessageProto() {
            @Override
            public int getJobId() {
                return 0;
            }
        };

        if(method.getReturnType().equals(Mono.class)) {
            return repositoryProvider.send(targetChannel, message)
                    .then(distributedJobTracker.createResultSink(jobId).asMono())
                    .transformDeferred(CircuitBreakerOperator.of(circuitBreaker)) // 🛡️ Protection !
                    .onErrorResume(CallNotPermittedException.class, ex ->
                            Mono.error(
                                    new RuntimeException("Le cluster est surchargé ou hors-ligne. Veuillez réessayer plus tard."))
                    );
        } else if (method.getReturnType().equals(Flux.class)) {
            distributedJobTracker.createUpdateSink(jobId).asFlux()
                    .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));
        }

        return null;
    }

    private Mono<Void> buildCompensationMono(Object proxy, String methodName, Object[] originalArgs) {
        return Mono.defer(() -> {
            try {
                Method compMethod = proxy.getClass().getMethod(methodName, getClasses(originalArgs));

                OrionPromise<?> promise = (OrionPromise<?>) compMethod.invoke(proxy, originalArgs);

                return ((ReactorPromiseAdapter<?>) promise).delegate().then();
            } catch (Exception e) {
                return Mono.error(e);
            }
        });
    }

    private Class<?>[] getClasses(Object[] args) {
        if (args == null) return new Class[0];
        Class<?>[] classes = new Class[args.length];
        for (int i = 0; i < args.length; i++) classes[i] = args[i].getClass();
        return classes;
    }
}
