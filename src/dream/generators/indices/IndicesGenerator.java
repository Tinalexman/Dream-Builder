package dream.generators.indices;

/**Used to generate the index buffer for a terrain.
 * @author Karl
 *
 */
public interface IndicesGenerator
{
	/**Generates the index buffer for a terrain.
	 * @param vertexCount - The number of vertices along one edge of the terrain.
	 * @return The index buffer as an array of ints.
	 */
	int[] generateIndexBuffer(int vertexCount);
}
