/*
 * Copyright (C) 2006-2014 Andrey Kholmanskih
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package jdbreport.util;

import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * @version 1.0 06/24/06
 * @author Andrey Kholmanskih
 * 
 */
public class Compress {

	public Compress() {
		super();
	}

	public static byte[] compress(byte[] input)
			 {
		byte[] output = new byte[input.length + 256];
		Deflater compresser = new Deflater();
		compresser.setInput(input);
		compresser.finish();
		int compressedDataLength = compresser.deflate(output);

		byte[] result = new byte[compressedDataLength];
		System.arraycopy(output, 0, result, 0, compressedDataLength);
		return result;
	}

	public static byte[] decompress(byte[] input) throws DataFormatException {
		Inflater decompresser = new Inflater();
		decompresser.setInput(input, 0, input.length);
		byte[] output = new byte[input.length * 10];
		int resultLength = decompresser.inflate(output);
		decompresser.end();

		byte[] result = new byte[resultLength];
		System.arraycopy(output, 0, result, 0, resultLength);
		return result;
	}

}
