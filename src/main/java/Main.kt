import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.observables.ConnectableObservable
import java.util.concurrent.TimeUnit


object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        andThen()
    }

   /*fun filterExample(){
       val observable: Observable<Int> = Observable.create {//1
           it.onNext(2)
           it.onNext(3)
           it.onNext(4)
           it.onNext(5)
           it.onComplete()
       }
       observable
               .any { it >=3 }
               .subscribe {  }
   }
*/
    private fun startWith() {//oK

        Observable.just("Spock", "McCoy")
                .startWith (
                        Observable.just("hola", "mundo")
                )
                .subscribe { item ->
                    println(item)
                }
    }

    private fun merge() {
        println("starting merge operator")

       val observable =  Observable.merge(
                (Observable.interval(1, TimeUnit.SECONDS)
                        .map { id: Long ->
                    "A$id"
                    //throw Exception()
                }),
                (Observable.interval(2, TimeUnit.SECONDS)
                         .map { id: Long -> "B$id" }))


                observable.subscribe({
                    println(it)
                }, {
                    print("ocurrió un error")
                })

        Thread.sleep(5000)
    }

    private fun concat() {
        val observableOne = Observable.create<String> {
            it.onNext("One")
            Thread.sleep(500)
            it.onNext("Two")
            Thread.sleep(500)
            it.onNext("Three")
            it.onComplete()
        }
        val observableTwo = Observable.create<String> {
            it.onNext("A")
            Thread.sleep(500)
            it.onNext("B")
            Thread.sleep(500)
            it.onNext("C")
            it.onComplete()
        }

        Observable.concat(observableOne, observableTwo)
                .subscribe {
                    println(it)
                }

    }

    private fun zip() {
        val firstNames = Observable.just("James", "Jean-Luc", "Benjamin")
        val lastNames = Observable.just("Kirk", "Picard", "James")

        firstNames.zipWith(
                lastNames,
                BiFunction { name: String?, lastName: String? ->
                    "The item is: $name $lastName"
                })
                .subscribe {
                    println(it)
                }

        Thread.sleep(3000)
    }

    private fun andThen() {
        val completableOne = Completable.create {
            Thread.sleep(2000)
            println("onComplete called")
            it.onComplete()
           //it.onError(java.lang.Exception("error"))
        }

        val observableTwo = Observable.create<String> {
            println("creating observable")
            it.onNext("One")
            Thread.sleep(1000)
            it.onNext("Two")
            Thread.sleep(1000)
            it.onNext("Three")
            it.onComplete()
        }

        val observable: Observable<String> =
                completableOne.andThen(observableTwo)
                observable.subscribe ({
                    println("subscribe $it")
                },{
                  println("An error ocurred: $it")
                })

        Thread.sleep(5000)
    }


    private fun cold(){
        val observable: Observable<String> = Observable.create {//1
            print("I´m creating data")
            it.onNext("Emitted 1")
            it.onNext("Emitted 2")
            it.onNext("Emitted 3")
            it.onNext("Emitted 4")
            it.onComplete()
        }
        observable.subscribe { s: String -> println("Observer1: $s") } //Observer2 subscribing

        observable.subscribe { s: String -> println("Observer2: $s") } //calling connect
    }
    private fun coldHot(){
        val observable: Observable<String> = Observable.create {//1
            print("I´m creating data")
            it.onNext("Emitted 1")
            it.onNext("Emitted 2")
            it.onNext("Emitted 3")
            it.onNext("Emitted 4")
            it.onComplete()
        }

        val connectableObservable: ConnectableObservable<String> = observable.publish()

        connectableObservable.subscribe { s: String -> println("Observer1: $s") }

        connectableObservable.subscribe { s: String -> println("Observer2: $s") }

        connectableObservable.connect() //starts emitting items to observers

    }

}