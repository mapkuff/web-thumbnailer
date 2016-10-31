package com.prime.web.thumbnailer.test.unit.repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.prime.web.thumbnailer.test.unit.AbstractSpringTest;

import io.prime.web.thumbnailator.domain.Image;
import io.prime.web.thumbnailator.factory.ImageIdFactory;
import io.prime.web.thumbnailator.factory.UUIDImageIdFactory;
import io.prime.web.thumbnailator.repository.ThumbnailatorImageRepository;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ThumbnailatorImageRepositoryTest extends AbstractSpringTest
{
	@Autowired
	private ThumbnailatorImageRepository repository;
	
	@PersistenceContext
	private EntityManager em;
	
	private ImageIdFactory factory = new UUIDImageIdFactory();
	
	private String imageId = factory.generate("sample_image_for_repository.jpg");
	
	@Test
	@Transactional
	public void A_imagePeristingTest()
	{
		Image image = new Image();
		image.setId(this.imageId);
		image.setData(new byte[]{11,12,13});
		this.repository.save(image);
		this.em.flush();
	}
	
	@Test
	@Transactional
	public void B_imageFechingTest()
	{
		Image image = this.repository.findOne(this.imageId);
		Assert.assertNotNull(image);
	}
	
	@Test(expected=NoResultException.class)
	@Transactional
	public void C_imageFechingErrorTest()
	{
		this.repository.findOne(this.imageId + ".bak");
	}
	
	
}
