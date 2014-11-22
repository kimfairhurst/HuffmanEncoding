import java.io.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class HuffmanEncoding{
    
    protected FileCharIterator fileCharIter;
    protected HashMap<String, Integer> frequencyMap;
    protected TreeMap<String, Integer> sortedFrequencyMap;
    protected BinaryTree HuffmanTree;
    protected HashMap<String, String> codeMap;
    protected String inputFile;
    protected BufferedReader br;
    protected FileFreqWordsIterator wordIterator;
    protected HashMap<String, Integer> wordFrequency;
    protected TreeMap<String, Integer> sortedWordFrequency;
 
    public HuffmanEncoding() {
        fileCharIter = null;
        frequencyMap = null;
        sortedFrequencyMap = null;
        HuffmanTree = null;
        wordIterator = null;
    }
    
    public HuffmanEncoding(String fileName) {
        fileCharIter = new FileCharIterator(fileName);
        frequencyMap = new HashMap<String, Integer>();
        inputFile = fileName;
    	codeMap = new HashMap<String, String>();
    	wordIterator = new FileFreqWordsIterator(fileName);
    	wordFrequency = new HashMap<String, Integer>();
    	sortedWordFrequency = new TreeMap<String, Integer>();
    }
    
    public void generateHuffmanTree() {
    	PriorityQueue<TreeNode> queue = new PriorityQueue<TreeNode>();
    	HuffmanTree = new BinaryTree();
    	Set<String> keys = sortedFrequencyMap.keySet();
    	Iterator<String> keyIterator = keys.iterator();
    	while (keyIterator.hasNext()) {
    		String str = keyIterator.next();
    		TreeNode toAdd = new TreeNode(str, frequencyMap.get(str));
    		queue.add(toAdd);
    	}
    	TreeNode root = null;
    	int count = 0;
    	while (!queue.isEmpty() && queue.size() != 1) {
    		TreeNode small1 = queue.remove();
    		TreeNode small2 = null;
    		if (!queue.isEmpty()) {
    			small2 = queue.remove();
    		} 
    		if (small2 == null) {
    			TreeNode combined = new TreeNode(small1.value + root.value);
    			combined.myRight = small1;
    			combined.myLeft = root;
    			small1.myParent = combined;
    			root.myParent = combined;
    			root = combined;
    			continue;
    		}
    		TreeNode combined = new TreeNode(small1.value + small2.value);
    		combined.myLeft = small1;
    		combined.myRight = small2;
    		small1.myParent = combined;
    		small2.myParent = combined;
    		queue.add(combined);
    		root = combined;
    	}
    	HuffmanTree.myRoot = root;
    }
    
    public void countFrequency() {
        while (fileCharIter.hasNext()) {
            String temp = fileCharIter.next();
            if (frequencyMap.get(temp) == null) {
                frequencyMap.put(temp, 1);
            } else {
                int count = frequencyMap.get(temp);
                count++;
                frequencyMap.put(temp, count);
            }
        }
        frequencyMap.put("EOF", 1);
        FrequencyComparator sorter = new FrequencyComparator(frequencyMap);
        sortedFrequencyMap = new TreeMap<String, Integer>(sorter);
        sortedFrequencyMap.putAll(frequencyMap);
    }
    
    public void countWordFrequency(int n) {
    	int counter = 0;
        while (wordIterator.hasNext()) {
        	System.out.println(counter);
            String temp = wordIterator.next();
            if (frequencyMap.get(temp) == null) {
                frequencyMap.put(temp, 1);
                if (temp.length() > 8) {
                	wordFrequency.put(temp, 1);
                }
            } else {
            	Integer key = frequencyMap.get(temp);
                frequencyMap.put(temp, key+1);
                if (temp.length() > 8) {
                	wordFrequency.put(temp, key+ 1);
                }
            }
            counter++;
        }
        frequencyMap.put("EOF", 1);
        System.out.println("Starting wordFrequency sorting");
        FrequencyComparator2 sorter1 = new FrequencyComparator2(wordFrequency);
        sortedWordFrequency = new TreeMap<String, Integer>(sorter1);
        sortedWordFrequency.putAll(wordFrequency);
        System.out.println("Ended wordFrequency sorting");
        Iterator iterator = sortedWordFrequency.keySet().iterator();
        try {
        	for (int i = 0; i < n; i++) {
        		iterator.next();
        	}
        	while (iterator.hasNext()) {
        		String key = (String)iterator.next();
        		int freq = frequencyMap.remove(key);
        		for (int i = 0; i < key.length() - 1; i = i + 8) {
        			String toAdd = key.substring(i, i + 8);
        			if (frequencyMap.get(toAdd) != null) {
        				int count = frequencyMap.get(toAdd);
        				count += freq;
        				frequencyMap.put(toAdd, count);
        			} else {
        				frequencyMap.put(toAdd, freq);
        			}
        		}
        	}
        } catch (NoSuchElementException e) {
        }
        FrequencyComparator sorter2 = new FrequencyComparator(frequencyMap);
        sortedFrequencyMap = new TreeMap<String, Integer>(sorter2);
        sortedFrequencyMap.putAll(frequencyMap);
    }
    
    public void encodeForZip(String fileName) throws IOException {
        File file = new File(fileName);
        file.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
        for (Map.Entry<String, String> map : codeMap.entrySet()) {
            writer.write(map.getKey());
            writer.write(",");
            writer.write(map.getValue());
            writer.write("\n");
        }
        writer.write("\n");
        writer.close();
        FileCharIterator secondIter = new FileCharIterator(inputFile);
        StringBuilder str = new StringBuilder("");
        while(secondIter.hasNext()) {
        	String binaryChar = secondIter.next();
        	str.append(codeMap.get(binaryChar));
        }
        str.append(codeMap.get("EOF"));
	    while (str.length() % 8 != 0) {
	    	str.append("0");
        }
        FileOutputHelper.writeBinStrToFile(str.toString(), fileName);
    }
    
    public void encode(String fileName) throws IOException {
        File file = new File(fileName);
        file.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for (Map.Entry<String, String> map : codeMap.entrySet()) {
            writer.write(map.getKey());
            writer.write(",");
            writer.write(map.getValue());
            writer.write("\n");
        }
        writer.write("\n");
        writer.close();
        FileCharIterator secondIter = new FileCharIterator(inputFile);
        StringBuilder str = new StringBuilder("");
        while(secondIter.hasNext()) {
        	String binaryChar = secondIter.next();
        	str.append(codeMap.get(binaryChar));
        }
        str.append(codeMap.get("EOF"));
	    while (str.length() % 8 != 0) {
	    	str.append("0");
        }
        FileOutputHelper.writeBinStrToFile(str.toString(), fileName);
    }
    
    public void encode2(String fileName) throws IOException {
        File file = new File(fileName);
        file.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for (Map.Entry<String, String> map : codeMap.entrySet()) {
            writer.write(map.getKey());
            writer.write(",");
            writer.write(map.getValue());
            writer.write("\n");
        }
        writer.write("\n");
        writer.close();
        FileFreqWordsIterator secondIter = new FileFreqWordsIterator(inputFile);
        StringBuilder str = new StringBuilder("");
        while(secondIter.hasNext()) {
        	String binaryChar = secondIter.next();
        	if (codeMap.get(binaryChar) == null) {
        		for (int i = 0; i < binaryChar.length() - 1; i = i + 8) {
        			str.append(codeMap.get(binaryChar.substring(i, i + 8)));
        		}
        	} else {
        		str.append(codeMap.get(binaryChar));
        	}
        }
        str.append(codeMap.get("EOF"));
	    while (str.length() % 8 != 0) {
	    	str.append("0");
        }
        FileOutputHelper.writeBinStrToFile(str.toString(), fileName);
    }
    
    public void decode(String dest) throws FileNotFoundException {
		StringBuilder output = new StringBuilder();
		StringBuilder binaryString = new StringBuilder();
		br = new BufferedReader(new FileReader(inputFile));
		HashMap<String, String> treeMap = new HashMap<String, String>();
		int index = 8;
		try {
			String current = br.readLine();
			while (current.contains(",")) {
				if (current.contains("EOF")) {
					String key = current.substring(4);
					String value = current.substring(0, 3);
					treeMap.put(key, value);
				} else {
					String key = current.substring(index + 1);
					String value = current.substring(0, index);
					treeMap.put(key, value);
				}
				current = br.readLine();

			}
			br.close();

			String prev = "";
			String curr = fileCharIter.next();

			while (!prev.equals("00001010") || !curr.equals("00001010")) {

				prev = curr;
				curr = fileCharIter.next();
			}
			curr = fileCharIter.next();
			// No Problem above.

			if (!fileCharIter.hasNext()) {
				binaryString.append(curr);
			} else {
				while (fileCharIter.hasNext()) {
					binaryString.append(curr);
					curr = fileCharIter.next();
				}
			}

			String digit = "";
			String stringBinary = binaryString.toString();
			int length = stringBinary.length();
			int i = 0;
			while (length != 0) {
				digit += stringBinary.substring(i, i + 1);
				if (treeMap.get(digit) != null) {
					if (treeMap.get(digit).equals("EOF")) {
						break;
					}
					output.append(treeMap.get(digit));
					digit = "";
				}
				length--;
				i++;
			}

			FileOutputHelper.writeBinStrToFile(output.toString(), dest);
		} catch (IOException e) {
			throw new NullPointerException("cannot find file");
		}
	}

    public void generateCodeWord(String s, TreeNode t, HashMap<String, String> codeMap) {
		if(t.myLeft != null || t.myRight != null) {
			if(t.myLeft != null) {
				generateCodeWord(s + "0", t.myLeft, codeMap);
			}
			if(t.myRight != null) {
				generateCodeWord(s + "1", t.myRight, codeMap);
			}
		} else {
			if (codeMap.get(t.key) != null) {
				return;
			} else {
				codeMap.put(t.key, s);
			}
		}
    }
    
    public static void main(String[] args) {
    	if (args.length == 3 && args[0].equals("encode")) {
    		HuffmanEncoding huffencoder = new HuffmanEncoding(args[1]);
    		huffencoder.countFrequency();
    		huffencoder.generateHuffmanTree();
    		huffencoder.generateCodeWord("", (TreeNode) huffencoder.HuffmanTree.myRoot, huffencoder.codeMap);
    		try {
				huffencoder.encode(args[2]);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	else if (args.length == 3 && args[0].equals("decode")) {
    		HuffmanEncoding huffencoder = new HuffmanEncoding(args[1]);
			try {
				huffencoder.decode(args[2]);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
    	}
    	else if (args.length == 4 && args[0].equals("encode2")) {
    		HuffmanEncoding huffencoder = new HuffmanEncoding(args[1]);
    		huffencoder.countWordFrequency(Integer.parseInt(args[3]));
    		huffencoder.generateHuffmanTree();
    		huffencoder.generateCodeWord("", (TreeNode) huffencoder.HuffmanTree.myRoot, huffencoder.codeMap);
    		try {
				huffencoder.encode(args[2]);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	} else {
    		return;
    	}
    }
  
    
  private class FrequencyComparator implements Comparator<String> {

        HashMap<String, Integer> sortedMap;
        
        public FrequencyComparator(HashMap<String, Integer> base) {
            this.sortedMap = base;
        }

        public int compare(String a, String b) {
            if (sortedMap.get(a) >= sortedMap.get(b)) {
                return 1;
            } else {
                return -1;
            }
        }
    }        
    
    private class FrequencyComparator2 implements Comparator<String> {

        HashMap<String, Integer> sortedMap;
        
        public FrequencyComparator2(HashMap<String, Integer> base) {
            this.sortedMap = base;
        }

        public int compare(String a, String b) {
            if (sortedMap.get(a) <= sortedMap.get(b)) {
                return 1;
            } else {
                return -1;
            }
        }
    }
    
}
