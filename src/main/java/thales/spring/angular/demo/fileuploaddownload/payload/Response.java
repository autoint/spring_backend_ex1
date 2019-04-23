package thales.spring.angular.demo.fileuploaddownload.payload;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode()
public class Response {
	private String fileName;
	private String fileDownloadUri;
	private String fileType;
	private long size;
}
