/**
 * Created by ytsegay on 11/5/2014.
 */

object Driver{
	def main (args: Array[String]) {
		val adjGraph = new GraphAdjacencyHashTable
		adjGraph.loadGraphFile("S:\\git\\NetProbe\\src\\main\\resources\\fig2.txt")

		adjGraph.initMessageMap
		//adjGraph.prettyPrint
		adjGraph.applyLfs(6)
	}
}

