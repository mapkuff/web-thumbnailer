# Web-Thumbnailator
- based on Reactive Stream API ([RxJava2](https://github.com/ReactiveX/RxJava)) and [Thumbnailator](https://github.com/coobird/thumbnailator)
- On the fly image processing such as image cropping or water mark adding by just change image request url. that's all.

# !!! UNDER DEVELOPMENT !!!
please wait...

# How Web-Thumbnailator work
For example, you want to store image and serve cropped 500x500 image in your home page.

1. Once you persist image, you will get `imageId`. store in somewhere. For example, in your Article table. 
2. And then request image to this pattern [/contextPath][/WebThumbnailatorBaseUrl]/filterName/imageId

Let's assume that you have
- `example.com` as your host name.
- `/sampleApp` as your contextPath.
- `/images` as a Web-Thumbnailator baseUrl.
- `crop_500x500` as your filter name to crop image to 500x500
- `/my/image/id/sample-article.jpg` as your imageId

then the url to serve the image which will be cropped to 500 x 500 would be `http://example.com/sampleApp/images/crop_500x500/my/image/id/sample-article.jpg`

# Installation
//TODO

# Spring
// TODO
	
# Servlet
[Go to servlet module](servlet)

# RxNetty
//TODO

# Filters Chain
when you define filter in configuration, you must define in list which will be trigger in chain. for example, your first filter may crop image and then your 2nd one will rotate your image.

# Provided Filters
1. `io.prime.web.thumbnailator.filter.CropFilter` 
//TODO add more filter

# Custom Filters
You can create your own filter by implements interface `ThumbnailatorFilter` and then add your implementation into configuration.
For more information about how to define your own filter, please read more about [Thumbnailator](https://github.com/coobird/thumbnailator)

# Usage -> Image Persisting		
1. Just autowire `io.prime.web.thumbnailator.util.ThumbnailatorUtil`
2. And then you can persist image using variety of overload methods of `ThumbnailatorUtil.create()` 
3. Once you persisted your image, you will get imageId as a `String`. store it wherever you want.

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
- email: map.siwapun@gmail.com

