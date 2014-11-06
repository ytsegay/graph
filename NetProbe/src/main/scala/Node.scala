/**
 * Created by ytsegay on 11/5/2014.
 */
class Node(val id:Int, mbeliefs:Array[Double]) {
	val beliefs = new Array[Double](3)
	for(i <- beliefs.indices) {beliefs(i) = 1.0}

	def setBeliefs(s: Array[Double]): Unit ={
		assert(s.size == beliefs.size)
		for(i <- s.indices){
			beliefs(i) = s(i)
		}
	}

	override def toString(): String ={
		return "[id: " + id + " (" + beliefs.mkString(", ") + ")]"
	}

}
