package generators.graph;


public class PageRankCalculator {

	int[][] adjacencyMatrix;
	float[] nodes;
	int[] outcomingEdges;

	
	float[][] G;
	
	float d;
	
	float delta = -1;
	
	public PageRankCalculator(int[][] adjacencyMatrix)
	{
		this(adjacencyMatrix, 0.85f);
	}
	
	public PageRankCalculator(int[][] adjacencyMatrix, float damping)
	{
		this.d = damping;
		//Initialize all matrix
		init(adjacencyMatrix);
	}
	
	
	/**
	 * Calculates the next values of the page rank algorithm for each node.
	 * Call getCurrentValues to access the new values after calling this function.
	 * 
	 * @return the delta value. This value represents how much the values changed.
	 */
	public float calcNextStep()
	{
		float[] result = new float[nodes.length];
		
		matrMultVec(G, nodes, result); // calculates the next values
		
		normalize(result); // normalise to prevent numerical errors to accumulate.
		
		delta = calcDelta(nodes, result); // calculates the delta: |X_(i-1) - X_(i)|
		
		nodes = result; 
		return delta;
	}
	
	/**
	 * @return The delta value. This value represents how much the values changed. 
	 * If the calcNextStep function isn't called once, the value will be invalid and the function will return -1.
	 * Otherwise the value will always be positive.
	 */
	public float getDelta()
	{
		return delta;
	}
	
	/**
	 * 
	 * @return The google matrix that will be used to calculate the new values.
	 */
	public float[][] getGoogleMatrix()
	{
		return G;
	}
	
	/**
	 * 
	 * @return The current values for each node.
	 */
	public float[] getCurrentValues()
	{
		return nodes;
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
	

	
	
	///////////////////////math section/////////////////////////////////////
	
	/**
	 * Norms the given vector. After that the sum of all values in the vector will be 1. 
	 * @param vector
	 */
	private void normalize(float[] vector)
	{
		float norm = norm(vector);
		
		if(norm != 0)
		{
			for(int i = 0; i< vector.length; i++)
			{
				vector[i] = vector[i]/norm;
			}
		}
	}
	
	/**
	 * Calculates the delta between the given vectors.
	 * @param vector1
	 * @param vector2
	 * @return the calculated delta
	 */
	private float calcDelta(float[] vector1, float[] vector2)
	{
		if(vector1.length != vector2.length)
			return -1;
		float tmpRes = 0;
		float tmp;
		for(int i = 0; i < vector1.length; i++)
		{
			tmp = vector1[i]-vector2[i];
			tmpRes += tmp*tmp;
		}
		
		return (float)Math.sqrt(tmpRes);
	}
	
	/**
	 * Calculates the L1-norm for the given vector
	 * @param vector
	 * @return the 1-norm
	 */
	private float norm(float[] vector)
	{
		float tmpRes = 0.0f;
		for(int i = 0; i < vector.length; i++)
		{
			tmpRes += vector[i];
		}	
		return Math.abs(tmpRes);
	}
	
	/**
	 * Calculates the L2-norm for the given vector
	 * @param vector
	 * @return
	 */
	private float norm2(float[] vector)
	{
		float tmpRes = 0.0f;
		for(int i = 0; i < vector.length; i++)
		{
			tmpRes += vector[i]*vector[i];
		}	
		return (float)Math.sqrt(tmpRes);
	}
	
	/**
	 * Calculates the matrix multiplication with a vector.
	 * The result will be stored in the result vector.
	 * The result vector musn't be the same vector as the given vector.
	 * @param matrix
	 * @param vector
	 * @param result
	 * @return
	 */
	private boolean matrMultVec(float[][] matrix, float[] vector, float[] result)
	{
		if(matrix.length != vector.length || vector.length != result.length)
			return false;
		
		float tmpRes;
		for(int i = 0; i< matrix[0].length; i++)
		{
			tmpRes = 0;
			for(int j = 0; j < matrix.length; j++)
			{
				tmpRes += matrix[j][i]*vector[j];
			}
			
			result[i] = tmpRes;
		}
		
		return true;
	}
	
	/////////////////////////To String Section///////////////////////////////////
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
				+ "\n\nnode values:"+ arrToString(nodes)
				+ "\n\ndelta: "+ delta;
		
	}
}
