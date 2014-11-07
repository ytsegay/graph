/**
 * Created by ytsegay on 11/5/2014.
 */

object Driver{
	def main (args: Array[String]) {

		//adjGraph.prettyPrint
		for (i <- 14 until 15) {
			val adjGraph = new GraphAdjacencyHashTable
			adjGraph.loadGraphFile("S:\\git\\NetProbe\\src\\main\\resources\\fig2.txt")

			println("\n\nMax iterations: " + i)
			adjGraph.initMessageMap(false)
			adjGraph.applyLfs(i)
		}
	}
}