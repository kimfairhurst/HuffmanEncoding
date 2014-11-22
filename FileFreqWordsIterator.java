import java.util.Iterator;


public class FileFreqWordsIterator implements Iterator<String> {

	protected FileCharIterator fileWordIter;
	public static final String NEWLINE = System.getProperty("line.separator"); 
	protected StringBuilder text;
	
	public FileFreqWordsIterator(String inputFileName) {
		fileWordIter = new FileCharIterator(inputFileName);
		text = new StringBuilder("");
		while (fileWordIter.hasNext()) {
			text.append(fileWordIter.next());
		}
	}
	
	@Override
	public boolean hasNext() {
		return text.length() != 0;
	}

	@Override
	public String next() throws IndexOutOfBoundsException {
		String str = "";
		String strNext = "";
		while (hasNext()) {
			str += text.substring(0, 8);
			try {
				strNext = text.substring(8, 16);
			} catch (StringIndexOutOfBoundsException e1) {
				text = new StringBuilder("");
				break;
			}
			text = new StringBuilder(text.substring(8));
			if (str.equals("00100000")) {
				break;
			}
			else if (str.equals("00001010")) {
				break;
			}
			else if (strNext.equals("00100000")) {
				break;
			}
			else if (strNext.equals("00001010")) {
				break;
			} else {
				continue;
			}
		}
		return str;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
	}
	
	/*
	public static void main(String[] args) {
		FileFreqWordsIterator iterator = new FileFreqWordsIterator("src/sample_files/SmallFile.txt");
		while (iterator.hasNext()) {
			System.out.println(iterator.next());
		}
	}
	*/
	
}
