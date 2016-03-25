import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * by qr
 * 2016/3/24
 */

/*
 * 识别思路：
 * 按行读取，先识别开头是否有浮点数。识别之后，识别结尾是否有可能的浮点数保存到last中，然后识别string中间段的浮点数。
 * 把文本末尾可能出现的浮点数last加到下一行文本的开头，继续识别直到遍历文本结束
 * 最终如果文本每一段最末尾出现可能的浮点数是无法识别的，再换段或者文本结束后再判断一次。
 */

/*
 * 测试文件的输出结果：
0.9
3.14159
-0.234
+23.98
0.009
99.99
0.009
A new paragraph begin.
0.009
0.009
A new paragraph begin.
1.11
A new paragraph begin.
-0.11
A new paragraph begin.
+0.99
A new paragraph begin.
A new paragraph begin.
A new paragraph begin.
A new paragraph begin.
A new paragraph begin.

 */

public class FloatDetection {

	public static void main(String[] args) {
		String pattern = "[^0-9.+-]([-+]?[0-9]+\\.[0-9]+)[^0-9.]";
		Pattern p = Pattern.compile(pattern);

		String filename = "case.txt";
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
			String str = "";
			String last = "";// 上一行剩下的。
			try {
				while ((str = br.readLine()) != null) {
					if (str.equals("")) {
						if (isRightFloat(last)) {
							System.out.println(last);
						}
						System.out.println("A new paragraph begin.");
						last = "";
						continue;
					}
					str = last + str;
//				    System.out.println("str="+str);
					str = getBegin(str);
					if (str.equals("")) {
						continue;
					}
					int index = getEnd(str);
					last = str.substring(index);
					str = str.substring(0, index);
					Matcher matcher = p.matcher(str);
					while (matcher.find()) {
						System.out.println(matcher.group(1)); // 识别第一个括号里面的东西
					}
				}
				// 最后判断是否结尾为可能的浮点数
				if (isRightFloat(last)) {
					System.out.println(last);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	// 得到末尾可能为浮点数的位置
	private static int getEnd(String str) {
		int len = str.length();

		int index = len - 1;
		char temp = str.charAt(index);
		while (index >= 0 && isFloat(temp)) {
			index--;
			if (index >= 0) {
				temp = str.charAt(index);
			} else {
				break;
			}
		}

		return index + 1;
	}

	// 去除开头的浮点数
	private static String getBegin(String str) {
		int len = str.length();

		int index = 0;
		char temp = str.charAt(index);
		while (isFloat(temp)) {
			index++;
			if (index < len) {
				temp = str.charAt(index);
			} else {
				break;
			}
		}

//		System.out.println("index="+index);
		

		String begin = str.substring(0, index);
//		System.out.println("begin="+begin);
		if (isRightFloat(begin)) {
			System.out.println(begin);
		}

		if (index == len)
			return "";
		
		return str.substring(index);
	}

	// 判断是否是正确形式的浮点数
	private static boolean isRightFloat(String begin) {
		return begin.matches("[-+]?[0-9]+\\.[0-9]+");
	}

	// 判断是否是浮点数中的字符
	private static boolean isFloat(char temp) {
		if ((temp <= '9' && temp >= '0') || temp == '.' || temp == '+' || temp == '-')
			return true;

		return false;
	}

}
