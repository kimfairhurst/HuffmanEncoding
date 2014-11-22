public class TreeNode {

	protected String key;
	protected int value;
	protected TreeNode myLeft;
	protected TreeNode myRight;
	protected TreeNode myParent;

	public TreeNode(String key, int value) {
		this.key = key;
		this.value = value;
		myLeft = myRight = null;
	}

	public TreeNode(String key, int value, TreeNode left, TreeNode right) {
		this.key = key;
		this.value = value;
		myLeft = left;
		myRight = right;
	}
	
	public TreeNode(int value) {
		this.key = null;
		this.value = value;
		myLeft = null;
		myRight = null;
		myParent = null;
	}
	
}
