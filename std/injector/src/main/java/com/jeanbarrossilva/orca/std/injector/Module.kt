package com.jeanbarrossilva.orca.std.injector

import kotlin.reflect.KClass

/** Container in which dependencies within a given context can be injected. **/
abstract class Module {
    /** Dependencies that have been injected associated to their assigned types. **/
    @PublishedApi
    internal val injections = hashMapOf<KClass<*>, Lazy<Any>>()

    /**
     * [NoSuchElementException] thrown if a dependency that hasn't been injected is requested to
     * be obtained.
     *
     * @param dependencyClass [KClass] of the requested dependency.
     **/
    inner class DependencyNotInjected
    @PublishedApi
    internal constructor(dependencyClass: KClass<*>) :
        NoSuchElementException(
            "No dependency of type ${dependencyClass.qualifiedName} has been injected into " +
                "${this::class.simpleName}."
        )

    /**
     * Injects the dependency returned by the [injection].
     *
     * @param T Dependency to be injected.
     * @param injection Returns the dependency to be injected.
     **/
    inline fun <reified T : Any> inject(noinline injection: Module.() -> T) {
        if (T::class !in injections) {
            injections[T::class] = lazy {
                injection()
            }
        }
    }

    /**
     * Lazily obtains the injected dependency whose type is [T].
     *
     * @param T Dependency to be lazily obtained.
     **/
    inline fun <reified T : Any> lazy(): Lazy<T> {
        return lazy(::get)
    }

    /**
     * Obtains the injected dependency whose type is [T].
     *
     * @param T Dependency to be obtained.
     * @throws DependencyNotInjected If no dependency of type [T] has been injected.
     **/
    @Throws(NoSuchElementException::class)
    inline fun <reified T : Any> get(): T {
        return injections[T::class]?.value as T? ?: throw DependencyNotInjected(T::class)
    }

    /** Removes all injected dependencies. **/
    fun clear() {
        injections.clear()
        onClear()
    }

    /** Callback run whenever this [Module] is cleared. **/
    internal open fun onClear() {
    }
}
