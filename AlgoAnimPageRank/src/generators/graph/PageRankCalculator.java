package generators.graph;


public class PageRankCalculator {

	int[][] adjacencyMatrix;
	float[] nodes;
	int[] outcomingEdges;

	
	float[][] G;
	
	float d;
	
	PageRankCalculator(int[][] adjacencyMatrix)
	{
		this(adjacencyMatrix, 0.85f);
	}
	
	PageRankCalculator(int[][] adjacencyMatrix, float d)
	{
		this.d = d;
		//Initialize all matrix
		init(adjacencyMatrix);
	}
	
	private void init(int[][] adjacencyMatrix)
	{
		// setting up the adjacemcyMatrix , the the beginning values for the nodes, and the outcommingEdges.
		// adjacemcyMatrix: values will be set to 0 or 1 depending of the values in the given matrix.
		// nodes: the beginning values will be set to 1/number of nodes.
		// outcomingEdges: set to the number of outcoming edges of the node.
		
		int numberOfNodes = adjacencyMatrix.length;
		
		this.adjacencyMatrix = new int[numberOfNodes][numberOfNodes];
		nodes = new float[numberOfNodes];
		outcomingEdges = new int[numberOfNodes];
		
		for(int i = 0; i < numberOfNodes; ++i)
		{
			nodes[i] = 1.0f/(float)numberOfNodes; // sets the beginning values of the nodes.
			for(int j = 0; j < numberOfNodes; ++j)
			{
				if(adjacencyMatrix[i][j] != 0)
				{
					this.adjacencyMatrix[i][j] = 1; // sets the adjacencyMatrix to 1 if the given matrix has a value other then 0 at the same position.
					//
					outcomingEdges[i] += this.adjacencyMatrix[i][j]; // count the number of outcoming edges.
					
				}else
				{
					this.adjacencyMatrix[i][j] = 0; // sets the adjacencyMatrix to 0 if the given matrix has a 0 too.
				}
			}
		}
		

		///// calculates the matrix G /////
		
		G = new float[numberOfNodes][numberOfNodes];
		
		for(int i = 0; i < numberOfNodes; ++i)
		{
			for(int j = 0; j < numberOfNodes; ++j)
			{
				if(outcomingEdges[i] == 0) // if edge is a dangling node.
				{
					G[i][j] = d*(1.0f/(float)numberOfNodes)+(1-d)/(float)numberOfNodes;
				}else
				{
					G[i][j] = d*((float)adjacencyMatrix[i][j]/(float)outcomingEdges[i])+(1.0f-d)/(float)numberOfNodes;
				}
				
			}
		}

	}
	

	
	
	private String matrToString(int[][] matrix)
	{
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < matrix.length; ++i)
		{
			sb.append("\n");
			for(int j = 0; j < matrix.length; ++j)
			{
				sb.append(matrix[j][i]).append(" ");
				
			}
		}
		return sb.toString();
	}
	
	private String matrToString(float[][] matrix)
	{
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < matrix.length; ++i)
		{
			sb.append("\n");
			for(int j = 0; j < matrix.length; ++j)
			{
				sb.append(matrix[j][i]).append(" ");
				
			}
		}
		return sb.toString();
	}
	
	private String arrToString(int[] array)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		for(int i = 0; i < array.length; ++i)
		{
			sb.append(array[i]).append(" ");
		}
		return sb.toString();
	}
	
	private String arrToString(float[] array)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		for(int i = 0; i < array.length; ++i)
		{
			sb.append(array[i]).append(" ");
		}
		return sb.toString();
	}
	
	public String toString()
	{
		
		return "adjacencyMatrix:"+matrToString(adjacencyMatrix)+"\n\nG:"+matrToString(G)
				+ "\n\noutcomingEdges:"+arrToString(outcomingEdges)
				+ "\n\nnode values:"+ arrToString(nodes);
		
	}
}
