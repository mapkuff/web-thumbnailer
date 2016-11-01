# web-thumbnailator
- On the fly image processing such as image cropping or water mark adding by just change image request url. that's it.
- This project is integrated with Spring and also storing data in DB using JPA is optional (for backup purpose).
- Btw we internally use Thumbnailator for image processing. specially thanks to that project that make us easier to do image processing tasks.
- support Servlet and will support Netty soon.

# Installation
- `checkout` this branch using git and then `mvn install`

# Configuration
1. Define spring configuration including web-thumbnailer configuration tag and define bean of type `io.prime.web.thumbnailator.filter.ThumbnailatorFilterSource`

     <?xml version="1.0" encoding="UTF-8"?>
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
		
# Usage
// TODO

# Roadmap
- need to review and do some tweaks before releasing.
- change to full async on processing request

# Contributor
Mr. Siwapun Siwaporn

job purpose email: siwapun.si@gmail.com

other purpose email: map.siwapun@gmail.com