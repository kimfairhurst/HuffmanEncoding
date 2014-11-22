import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TreeMap;

public class Zipper {

	protected FileCharIterator fileCharIter;
	protected String inputFile;
	protected String inputDir;
	protected BufferedReader br;

	public Zipper(String fileName, int x) {
		if (x == 0) {
			inputDir = fileName;
		} else {
			inputFile = fileName;
			fileCharIter = new FileCharIterator(fileName);
		}
	}

	public static void main(String[] args) {
		if (args.length == 3 && args[0].equals("zipper")) {
			Zipper huffencoder = new Zipper(args[1], 0);
			try {
				huffencoder.zipper(args[2]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (args.length == 3 && args[0].equals("unzipper")) {
			Zipper huffencoder = new Zipper(args[1], 1);
			try {
				huffencoder.unzipper(args[2]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			return;
		}
	}

	public void zipper(String outputFileName) throws IOException {

		File directory = new File(inputDir);
		TreeMap<File, Integer> list = new TreeMap<File, Integer>();
		String trashName = "trashFile";

		// create listOfFiles to have all the file and directory names
		// the matching listOfLengths should contain the lengths of the
		// compressed files
		if (directory.isDirectory()) {
			list.put(directory, -1);
			for (File outerFile : directory.listFiles()) {
				if (outerFile.isDirectory()) {
					list.put(outerFile, -1);
					for (File innerFile : outerFile.listFiles()) {
						list.put(innerFile, 0);
					}
				} else {
					list.put(outerFile, 0);
				}
			}
		} else {
			list.put(directory, 0);
		}

		int counter = 0;
		Iterator<File> iterator2 = ((Iterable<File>) list.keySet()).iterator();

		for (File a = iterator2.next(); iterator2.hasNext(); a = iterator2
				.next()) {
			File trashFile = new File(trashName);
			trashFile.createNewFile();
			if (a.isFile()) {
				// change to actual path
				HuffmanEncoding huffencoder = new HuffmanEncoding(
						a.getAbsolutePath());
				huffencoder.countFrequency();
				huffencoder.generateHuffmanTree();
				huffencoder.generateCodeWord("",
						(TreeNode) huffencoder.HuffmanTree.myRoot,
						huffencoder.codeMap);
				try {
					String name = trashFile.getName();
					huffencoder.encode(name);
					FileCharIterator fileCharIter = new FileCharIterator(name);
					while (fileCharIter.hasNext()) {
						counter++;
						fileCharIter.next();
					}
					counter++;
					list.put(a, counter);
					fileCharIter.closeStream();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				continue;
			}
			trashFile.delete();
		}
		if (list.lastKey().isFile()) {
			// change to actual path
			HuffmanEncoding huffencoder = new HuffmanEncoding(list.lastKey()
					.getAbsolutePath());
			huffencoder.countFrequency();
			huffencoder.generateHuffmanTree();
			huffencoder.generateCodeWord("",
					(TreeNode) huffencoder.HuffmanTree.myRoot,
					huffencoder.codeMap);
			try {
				File trashFile = new File(trashName);
				trashFile.createNewFile();
				String name = trashFile.getName();
				huffencoder.encode(name);
				FileCharIterator fileCharIter = new FileCharIterator(name);
				while (fileCharIter.hasNext()) {
					counter++;
					fileCharIter.next();
				}
				list.put(list.lastKey(), (Integer) counter);
				fileCharIter.closeStream();
				trashFile.delete();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// write the table of contents
		File createdFile = new File(outputFileName);		
		BufferedWriter writer = new BufferedWriter(new FileWriter(createdFile));

		int counter2 = 0;
		
		for (File b : list.keySet()) {		
				writer.write(b + "," + list.get(b));
				writer.write("\n");
				counter2 = counter2 + (int)list.get(b) +1;
		}
		// write a new line to begin the compressed files
		writer.write("\n");
		writer.close();

		// insert the compressed version of each of the files
		for (File c : list.keySet()) {
			if (c.isFile()) {
				HuffmanEncoding huffencoder = new HuffmanEncoding(c.getAbsolutePath());
				huffencoder.countFrequency();
				huffencoder.generateHuffmanTree();
				huffencoder.generateCodeWord("",(TreeNode) huffencoder.HuffmanTree.myRoot,
						huffencoder.codeMap);
				try {
					huffencoder.encodeForZip(outputFileName);
					BufferedWriter writer2 = new BufferedWriter(new FileWriter(createdFile, true));
					writer2.write("\n");
					writer2.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				continue;
			}
		}
	}

	public void unzipper(String outputFileName) throws IOException {
		FileReader abd = new FileReader(inputFile);

		br = new BufferedReader(abd);

		TreeMap<File, Integer> treeMap = new TreeMap<File, Integer>();
		TreeMap<String, Integer> treeMapString = new TreeMap<String, Integer>();

		File storageDir = new File(outputFileName);
		storageDir.mkdir();

		try {
			String current = br.readLine();

			// make new directory for placing all items
			while (current.contains(",")) {
				int index = current.indexOf(",");
				String key = current.substring(0, index);
				// if is a directory
				if (current.contains("-1")) {
					File currentDir = storageDir;
					String tester = key;
					while (tester.contains("\\")) {
						int index3 = tester.indexOf("\\");
						String keySub = tester.substring(0, index3);
						File thisDir = new File(currentDir, keySub);
						thisDir.mkdir();
						currentDir = thisDir;
						tester = tester.substring(index3 + 1);
					}
					treeMap.put(currentDir, -1);

					treeMapString.put(currentDir.getAbsolutePath(), -1);

					// if is a file
				} else {
					File currentDir = storageDir;
					String tester2 = key;
					while (tester2.contains("/")) {

						int index4 = tester2.indexOf("/");
						String keySub2 = tester2.substring(0, index4);
						File thisDir2 = new File(currentDir, keySub2);
						thisDir2.mkdir();
						currentDir = thisDir2;
						tester2 = tester2.substring(index4 + 1);
					}
					File thisFile2 = new File(currentDir, tester2);
					thisFile2.createNewFile();
					Integer value = Integer.parseInt(current
							.substring(index + 1));
					treeMap.put(thisFile2, value);
					treeMapString.put(thisFile2.getAbsolutePath(), value);
				}
				current = br.readLine();
			}
			br.close();

			ValueComparator fc = new ValueComparator(treeMap);
			TreeMap<File, Integer> finalTree = new TreeMap<File, Integer>(fc);
			FrequencyComparator vs = new FrequencyComparator(treeMapString);
			TreeMap<String, Integer> finalTree2 = new TreeMap<String, Integer>(
					vs);

			finalTree.putAll(treeMap);
			finalTree2.putAll(treeMapString);

			// initialize fileCharIter to the new line characters
			String prev = "";
			String curr = fileCharIter.next();

			while (!prev.equals("00001010") || !curr.equals("00001010")) {
				prev = curr;
				curr = fileCharIter.next();
			}

			int counter4 = 0;

			Iterator<String> iterator2 = finalTree2.keySet().iterator();

			Collection<Integer> c = finalTree2.values();
			Iterator<Integer> itr = c.iterator();
			long bitMap = itr.next();
			boolean last=false;
			while (iterator2.hasNext()) {
				String outputToDecode = iterator2.next();
				long bitMapNext;
				try {
					bitMapNext = itr.next();
				} catch (NoSuchElementException e) {
					last=true;
					bitMapNext=0;
				}
				if (bitMap != -1) {
					String trash = "trashFile.txt";
					File trashFile = new File(trash);
					FileWriter writer = new FileWriter(trashFile);

					// copy contents into trash file
					StringBuilder stringFile = new StringBuilder();
					

					while (counter4 < bitMapNext || (last==true && fileCharIter.hasNext())) {
						stringFile.append(fileCharIter.next());
						counter4++;
					}
					
					FileOutputHelper.writeBinStrToFile(stringFile.toString(),trash);

					// decode trash file
					HuffmanEncoding huffencoder = new HuffmanEncoding(
							"trashFile.txt");
					try {
						huffencoder.decode(outputToDecode);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}

					// delete trash file
					writer.close();
					trashFile.delete();
					stringFile= new StringBuilder();

				} 
				bitMap = bitMapNext;
			}

		} catch (IOException e) {
			throw new NullPointerException("cannot find file");
		}
	}

	private class ValueComparator implements Comparator<File> {

		TreeMap<File, Integer> sortedMap;

		public ValueComparator(TreeMap<File, Integer> base) {
			this.sortedMap = base;
		}

		public int compare(File a, File b) {
			if (sortedMap.get(a) >= sortedMap.get(b)) {
				return 1;
			} else {
				return -1;
			}
		}
	}

	private class FrequencyComparator implements Comparator<String> {

		TreeMap<String, Integer> sortedMap;

		public FrequencyComparator(TreeMap<String, Integer> treeMapString) {
			this.sortedMap = treeMapString;
		}

		public int compare(String a, String b) {
			if (sortedMap.get(a) >= sortedMap.get(b)) {
				return 1;
			} else {
				return -1;
			}
		}
	}
}
