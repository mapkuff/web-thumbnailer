//package io.prime.web.thumbnailator.repository;
//
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//import javax.persistence.TypedQuery;
//
//import org.springframework.transaction.annotation.Transactional;
//
//import io.prime.web.thumbnailator.domain.Image;
//
//public class ThumbnailatorImageRepositoryImpl implements ThumbnailatorImageRepository
//{
//	@PersistenceContext
//	private EntityManager em;
//
//	@Transactional(readOnly=true)
//	public Image findOne(String imageId) 
//	{
//		TypedQuery<Image> query = this.em.createQuery(
//				String.format(
//					"SELECT e FROM %s e where e.id = ?1",
//					Image.class.getName()
//				), 
//				Image.class
//			);
//		query.setParameter(1, imageId);
//		return query.getSingleResult();
//	}
//	
//	@Transactional
//	public void save(Image image)
//	{
//		this.em.persist(image);
//	}
//	
//}
