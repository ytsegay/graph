/**
 * Created by ytsegay on 10/29/2014.
 */

/*
// https://code.google.com/p/jure224/source/browse/trunk/Netprobe.py?r=9
// http://repository.cmu.edu/cgi/viewcontent.cgi?article=1530&context=compsci
 */
import scala.collection.mutable.ListBuffer

class GraphAdjacencyHashTable {
	private val graphMap = scala.collection.mutable.LinkedHashMap.empty[Int, ListBuffer[Int]]
	private var messagesMap =  scala.collection.mutable.LinkedHashMap.empty[String, Array[Double]]
	private val nodesMap = scala.collection.mutable.LinkedHashMap.empty[Int, Node]
	private var countOfStates = 0


	def addNode(from:Int, to:Int) = {
		val lst:ListBuffer[Int] = graphMap.getOrElse(from, ListBuffer[Int]())
		lst += to
		graphMap(from) = lst
	}


	def initMessageMap(): Unit ={
		val propagationMatrix = generatePropagationMatrix

		for ((k,v) <- graphMap) {
			for (node <- v) {
				val msgKey: String = (generateEdgeId(k, node))

				val initArr = new Array[Double](countOfStates)
				for (a <- 0 until countOfStates){
					var beliefSum = 0.0
					for (b <- 0 until countOfStates){

						beliefSum += (propagationMatrix(b)(a)*nodesMap(node).beliefs(b))
					}
					initArr(a) = beliefSum
				}
				messagesMap(msgKey) = initArr
			}
		}
	}


	def applyLfs(iterationsCount:Int): Unit = {
		val propagationMatrix = generatePropagationMatrix
		val newMessagesMap = scala.collection.mutable.LinkedHashMap.empty[String, Array[Double]]

		for (iterations <- 0 until iterationsCount) {
			//println("\niteration: " + iterations)
			for ((i, v) <- graphMap) {
				for (j <- v) {
					val msg = generateMessage(i, j, propagationMatrix)
					//println("msg: " + generateEdgeId(i, j) + " => " + msg.mkString(", "))

					// propagate message to j
					newMessagesMap(generateEdgeId(i, j)) = msg
				}
			}
			messagesMap = newMessagesMap
		}
		computeBeliefs()
	}


	def generateMessage(fromNode:Int, toNode:Int, propagationMatrix:Array[Array[Double]]) :Array[Double] = {
		// prepare message from i -> j
		var msg = new Array[Double](countOfStates)
		for (sigma <- 0 until countOfStates) {
			var sum = 0.0
			for (sigmaPrime <- 0 until countOfStates) {
				var prod = 1.0
				for (n <- graphMap(fromNode)) {
					if (n != toNode) {
						prod *= messagesMap(generateEdgeId(n, fromNode))(sigmaPrime)
					}
				}
				sum += (propagationMatrix(sigmaPrime)(sigma) * prod)
			}
			msg(sigma) = sum
		}
		// normalize to avoid getting to zero prob. quickly
		msg = normalize(msg)
		msg
	}


	def computeBeliefs() = {
		for ((i, v) <- graphMap) {
			var beliefs = nodesMap(i).beliefs

			for(sigma <- 0 until countOfStates) {
				var prod = 1.0
				for (j <- v) {
					prod *= messagesMap(generateEdgeId(j, i))(sigma)
				}
				beliefs(sigma) = prod
			}
			beliefs = normalize(beliefs)
			println(i + ": " + beliefs.mkString(", ") + " " + mapBeliefs(beliefs))
		}
	}


	private def mapBeliefs(msg:Array[Double]) :String = {
		var maxI = 2
		var maxVal = msg(maxI)
		val values = Array("FRAUD", "ACCOMPLICE", "HONEST")

		for(i <- msg.indices){
			if (msg(i) > maxVal){
				maxI = i
				maxVal = msg(i)
			}
		}
		values(maxI)
	}


	//******* Helpers ********//
	// propagation matrix of known user behavior as specified in the paper
	private def generatePropagationMatrix(): Array[Array[Double]] ={
		val eP = 0.05
		Array(
			Array(eP, 1.0 - (2.0*eP), eP),
			Array(0.5, 2.0*eP, 0.5 - 2*eP),
			Array(eP, (1.0 - eP)/2.0, (1.0 - eP)/2.0)
		)
	}


	private def generateEdgeId(i:Int, j:Int) :String = {
		("" + i + "," + j)
	}


	// helper to do array multiplication
	private def multiplyArrays(arr:Array[Double], arr2:Array[Double]) :Array[Double] = {
		val x = for(i <- arr.indices) yield arr(i)*arr2(i)
		x.toArray
	}


	// helper to do array division
	private def divideArrays(arr:Array[Double], arr2:Array[Double]) :Array[Double] = {
		val x = for(i <- arr.indices) yield arr(i)/arr2(i)
		x.toArray
	}


	private def normalize(msg:Array[Double]):Array[Double] = {
		val sum = msg.sum
		if (sum > 0.0){
			val x = for(i <- msg.indices) yield msg(i)/sum
			return x.toArray
		}
		throw new Exception("Failed to normalize")
	}


	def prettyPrint(): Unit ={
		for ((k,v) <- graphMap) {
			print("[" + k + "] ==> ")
			for(node <- v){
				print(node.toString)
			}
			println
		}
	}


	def loadGraphFile(filePath:String): Unit ={

		val lines = io.Source.fromFile(filePath).getLines().toList
		var fileSegment = "priorBeliefs"
		for (line <- lines) {
			// ignore comment lines
			if (!line.startsWith("#")) {
				if (line.startsWith("==========")) {
					fileSegment = "graphDef"
				}
				else if (fileSegment == "priorBeliefs") {
					val parts = line.toString.split("->")
					val nodeKey = parts(0).trim.toInt
					val strBeliefs = parts(1).trim.stripPrefix("(").stripSuffix(")").split(",")
					val beliefs = strBeliefs.map(_.trim.toDouble)

					if (!nodesMap.contains(nodeKey)) {
						val node = new Node(nodeKey, beliefs)
						nodesMap(nodeKey) = node
						countOfStates = beliefs.size
					}
				}
				else if (fileSegment == "graphDef") {
					val parts = line.toString.split("=>")
					val nodeKey = parts(0).trim
					val toNodes = parts(1).trim.split(",")

					for(toNode <- toNodes) {
						assert(nodesMap.contains(toNode.trim.toInt) && nodesMap.contains(nodeKey.toInt))
						addNode(nodeKey.trim.toInt, toNode.trim.toInt)
					}
				}
			}
		}
	}
}
