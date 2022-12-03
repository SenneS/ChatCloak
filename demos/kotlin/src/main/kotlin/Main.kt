package be.senne.kotlin

import java.awt.Point
import kotlin.random.Random


//in tegenstelling tot java is kotlin minder object gebonden, functies moeten dus niet perse in een class staan.

//variabelen moeten geinit worden in een ctor of bij de declaratie bh als het keyword lateinit er naast staat.
//var variable0 : Point //ERROR
lateinit var variable0 : Point //OK


//functies worden aangeduid met 'fun'
//variabelen/parameters zijn in het formaat {naam}: {type}
fun main(args: Array<String>) {

    //korter java's dan System.out.println
    println("Hello World!")

    //variabelen kunnen gemaakt worden met var en val
    //var variabelen kunnen aangepast worden
    //val variabelen zijn constant
    var variable1 = 1
    val variable2 = 2
    variable1++
    println(variable1)
    //variable2++ //ERROR


    // Loops zijn een beetje anders in kotlin.
    //hier loopen we over het interval [1, 3]
    for (i in 1..3) {
        if(i == 1) {
            //er zijn ook andere loops
            //hier loopen we over het interval [1, 3[
            for (ii in 1 until 3) {
                println("[0] ii = $ii")
            }
            //de increment stap kan ook benvloed worden
            //dit komt overeen met ii+=2
            for (ii in 1..5 step 2) {
                println("[1] ii = $ii")
            }

            var list1 = listOf(5, 4, 3, 2, 1, 0)

            //loopen over collections is ook een beetje anders
            for(item in list1) {
                println("list1 item -> $item")
            }
            //het kan ook met een index gedaan worden
            for((index, item) in list1.withIndex()) {
                println("list1 item pos @ $index -> $item")
            }

        }


        // Een dollar teken kan gebruikt worden voor string interpolation
        println("i = $i")

        //Kotlin's switches zijn een beetje anders
        val getal = Random.nextInt(0, 10)
        when(getal) {
            0 -> {
                println("0")
            }
            1 -> {
                println("1")
            }
            2, 3 -> {
                println("2 of 3")
            }
            in 4..7 -> {
                println("4, 5, 6 of 7")
            }
            else ->  {
                println("Anders")
            }
        }
    }

    //variabelen kunnen worden gelazyload, dwz de code gaat enkel uitgevoerd worden wanneer de variabele nodig is en maar enkel 1 keer.
    val x : Int by lazy {
        println("lazily getting x")
        9
    }
    println("Arrr")
    println("x marks the spot: $x")
    println("did you find the treasure?")
    println("x didn't mark the spot: $x")

    //zie extension functie uitleg
    println("dit is een extension test.".mijnStringFunctie())


    castingFunctie("hey")
    castingFunctie(1)
    castingFunctie(Point())

    //bit operators zijn niet zoals in java/c (bleh)
    //| -> or
    //& -> and
    //^ -> xor
    //>> -> shr
    //<< -> shl
    var operatorTest0 = 1 or 2 //3
    var operatorTest1 = 412 and 356 //260
    var operatorTest2 = 0x7F xor 0x36 //73
    var operatorTest3 = 8 shr 2 //32
    var operatorTest4 = 8 shl 2 //2

    println(operatorTest0)
    println(operatorTest1)
    println(operatorTest2)
    println(operatorTest3)
    println(operatorTest4)


}

//je kan objecten gemakkelijk extended met extra functies
fun String.mijnStringFunctie() : String {
    return "-> [$this] <-";
}

//kotlin is beter in objecten casten dan java, de compiler weet bv dat de logica na (eenObject is String) enkel uitgevord -
//is als het een String was en laat dus toe om het object in de branche te gebruiken als string zonder expliciet te moeten casten.
fun castingFunctie(eenObject : Any) {
    if(eenObject is String) {
        println("het was een string: ${eenObject.uppercase()}")
    }
    else if(eenObject is Int) {
        println("het was een int: ${eenObject.inc()}")
    }
    else {
        println("het was een ander object: ${eenObject}")
    }
}