# Web-Thumbnailator
- On the fly image processing such as image cropping or water mark adding by just change image request url. that's it.
- This project is integrated with [Spring](https://github.com/spring-projects/spring-framework) and also storing data in DB using `JPA` is optional (for backup purpose).
- Btw we internally use [Thumbnailator](https://github.com/coobird/thumbnailator) for image processing. specially thanks to that project that make us easier to do image processing tasks.
- support `Servlet` and will support [Netty](https://github.com/netty/netty) soon.

# Installation
`checkout` this branch using git and then `mvn install` then add dependency
     
         <dependencies>
             <dependency>
			       <groupId>io.prime.th</groupId>
			       <artifactId>web.thumbnailator</artifactId>
             </dependency>
         </dependencies>		

# Configuration -> Spring
1. Define spring configuration including web-thumbnailer configuration tag and define bean of type `io.prime.web.thumbnailator.filter.ThumbnailatorFilterSource`

     	<beans xmlns="http://www.springframework.org/schema/beans"
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xmlns:thmb="http://www.prime.in.th/schema/web/thumbnailator"
			xsi:schemaLocation="
				http://www.springframework.org/schema/beans 
				http://www.springframework.org/schema/beans/spring-beans-4.2.xsd
		       http://www.prime.in.th/schema/web/thumbnailator
		       http://www.prime.in.th/schema/web/thumbnailator.xsd
		       ">
				
			<thmb:web-thumbnailer 	source-image-directory="src/test/resources/assets/img-src" 
									filtered-image-directory="src/test/resources/assets/img-filtered" 
									base-url="/image" 
									database-enabled="true"
								/>
			
			<bean class="io.prime.web.thumbnailator.filter.ThumbnailatorFilterSource">
				<property name="filters">
					<map>
						<entry key="crop_500x500">...</entry>
						<entry key="crop_600x600">...</entry>
					</map>
				</property>
			</bean>
			
		</beans>

`source-image-directory` and `filtered-image-directory` will be loaded using [Spring ResourceLoader](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/resources.html). see them for more information and learn how can you specify it.
	
# Configuration -> Servlet
//TODO

# Configuration -> Netty
//TODO

# Filters Chain
when you define filter in configuration, you must define in list which will be trigger in chain. for example, your first filter may crop image and then your 2nd one will rotate your image.

# Provided Filters
1. `io.prime.web.thumbnailator.filter.CropFilter` //TODO

# Custom Filters
You can create your own filter by implements interface `ThumbnailatorFilter` and then add your implementation into configuration.

# Usage -> Image Persisting		
1. Just autowire `io.prime.web.thumbnailator.util.ThumbnailatorUtil`
2. And then you can persist image using variety of overload methods of `ThumbnailatorUtil.create()` 
3. Once you persisted your image, you will get imageId as a `String`. store it wherever you want.

# Usage -> Image Serving
Assume that you have
- `example.com` as your host name.
- `/sampleApp` as your contextPath.
- `/images` as a Web-Thumbnailator baseUrl.
- `crop_500x500` as your filter name to crop image to 500x500
- `/my/image/id/sample.jpg` as your imageId

then the url to serve the image which will be cropped to 500 x 500 would be `http://example.com/sampleApp/images/crop_500x500/my/image/id/sample.jpg`

# Usage -> Programmatic
Once you autowire `io.prime.web.thumbnailator.util.ThumbnailatorUtil` then you can use
- `ThumbnailatorUtil.create()` to persist image
- `ThumbnailatorUtil.get()` to get filtered image
- `ThumbnailatorUtil.getSource()` to get source image

# Roadmap
- need to review and do some tweaks before releasing.
- change to full async on processing request
- provide full configuration on every part of this project.

# Contributor
Mr. Siwapun Siwaporn
- job purpose email: siwapun.si@gmail.com
- other purpose email: map.siwapun@gmail.com
- (LinkedIn)[https://www.linkedin.com/in/siwapun-siwaporn-3b060594]

