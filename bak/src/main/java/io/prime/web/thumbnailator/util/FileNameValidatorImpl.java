//package io.prime.web.thumbnailator.util;
//
//import org.springframework.util.Assert;
//import org.springframework.util.StringUtils;
//
//import io.prime.web.thumbnailator.util.exception.InvalidFileNameException;
//
////TODO change the way to validate file name using whitelist characters
//public class FileNameValidatorImpl implements FileNameValidator
//{
//	private final String[] RESERVED_WORDS = new String[]{"/", "\\"};
//	
//	@Override
//	public void validate(String fileName) 
//	{
//		try {
//			Assert.isTrue(StringUtils.hasLength(fileName), "File name must not be empty");
//			for(String reservedWord : this.RESERVED_WORDS) {
//				Assert.isTrue(-1 == fileName.indexOf(reservedWord), "fileName must not contains reserved word: " + reservedWord);
//			}
//		} catch (IllegalArgumentException e) {
//			throw InvalidFileNameException.fromContainingReservedWord(fileName, this.RESERVED_WORDS);
//		}
//	}
//}
