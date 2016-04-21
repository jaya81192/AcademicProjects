package pack1;
import java.util.ArrayList;
import java.util.HashMap;
public class TreeNode 
{
	//A treenode is a node which holds the set of tuples belonging to the node after prior classifications 
	public ArrayList<TreeNode> childNodes;
	public String classAttribute;
	public String classAttributeValue;
	public TreeNode parentNode;
	public HashMap<String, Integer> classHash;
	public TreeNode() 
	{
		childNodes = new ArrayList<TreeNode>();
		classHash = new HashMap<String, Integer>();
	}
	
}
