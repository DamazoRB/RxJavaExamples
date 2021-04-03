import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

object Example {
    @JvmStatic
    fun main(args: Array<String>) {
        disposables()
    }

    private fun creational() {
        /**
         * Observer
         */
        val observer: Observer<String> = object : Observer<String> {
            override fun onComplete() {
                println("All Completed")
            }

            override fun onNext(item: String) {
                println("Next $item")
            }

            override fun onError(e: Throwable) {
                println("Error Occurred => ${e.message}")
            }

            override fun onSubscribe(d: Disposable) {
                println("New Subscription ")
            }
        }

        val observerList = object : Observer<List<String>> {
            override fun onSubscribe(d: Disposable) {
                println("New Subscription")
            }

            override fun onNext(item: List<String>) {
                println("onNext = ${item.joinToString { it }}")
            }

            override fun onError(e: Throwable) {
                println("Error Occurred => ${e.message}")
            }

            override fun onComplete() {
                println("All Completed")
            }

        }
/*
        /**
         * Create
         */
        val observable: Observable<String> = Observable.create {//1
            it.onNext("Emitted 1")
            it.onNext("Emitted 2")
            it.onNext("Emitted 3")
            it.onNext("Emitted 4")
            it.onComplete()
        }
        observable.subscribe(observer)

        val observable2: Observable<String> = Observable.create {//2
            it.onNext("Emitted 1")
            it.onNext("Emitted 2")
            it.onError(Exception("My Exception"))
        }
        observable2.subscribe(observer)


        /**
         * Just
         */

        val observableJust = Observable.just(list)
        observableJust.subscribe(observerList)

        /**
         * FromIterable
         */
        val list = listOf("Str 1", "Str 2", "Str 3", "Str 4")
        val observableFromIterable: Observable<String> = Observable.fromIterable(list)
        observableFromIterable.subscribe(observer)
*/
       /**
         * FromCallable
         */
        val callable = Callable<String> {
           println(Thread.currentThread().name)
           "I'm From Callable"
       }
        val observableFromCallable = Observable.fromCallable(callable)
        observableFromCallable
                .observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.io())
                .subscribe ({
                    println("is successful")
                    println(Thread.currentThread().name)
                },{ error ->
                    print("error")
    })

        Thread.sleep(2000)

        /**
         * A completable from observable
         */
        val observable = Observable.just("Uno", "Dos")
        val completable = Completable.fromObservable(observable)
        completable.subscribe {
            print("Successful")
        }

        val completableTwo = observable
                .ignoreElements()
                .subscribe {
                    print("Successful")
                }
    }

    private fun disposables() {
        val observable = Observable.interval(100, TimeUnit.MILLISECONDS)//1

        val observer = object : Observer<Long> {
            lateinit var disposable: Disposable//2

            override fun onSubscribe(d: Disposable) {
                disposable = d//3
            }

            override fun onNext(item: Long) {
                println("Received $item")
                if (item >= 10 && !disposable.isDisposed) {//4
                    disposable.dispose()//5
                    println("Disposed")
                }
            }

            override fun onError(e: Throwable) {
                println("Error ${e.message}")
            }

            override fun onComplete() {
                println("Complete")
            }
        }

        observable.subscribe(observer)
        Thread.sleep(1500)


    }
}