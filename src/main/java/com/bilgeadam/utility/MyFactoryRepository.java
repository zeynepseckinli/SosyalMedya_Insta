package com.bilgeadam.utility;

import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MyFactoryRepository<T,ID> implements ICrud<T,ID>{
    private Session session;
    private Transaction transaction;
    private CriteriaBuilder criteriaBuilder;
    private EntityManager entityManager;
    T t;

    public MyFactoryRepository(T entity){
        entityManager = HibernateUtility.getSessionFactory().createEntityManager();
        criteriaBuilder = entityManager.getCriteriaBuilder();
        this.t = entity;
    }
    private void openSession(){
        session = HibernateUtility.getSessionFactory().openSession();
        transaction = session.beginTransaction();
    }
    private  void closeSession(){
        transaction.commit();
        session.close();
    }


    @Override
    public T save(T entity) {
        openSession();
        session.save(entity);
        closeSession();
        return entity;
    }

    @Override
    public Iterable<T> saveAll(Iterable<T> entites) {
        openSession();
        entites.forEach(t->{
            session.save(t);
        });
        closeSession();
        return entites;
    }

    @Override
    public void delete(T entity) {
        openSession();
        session.delete(entity);
        closeSession();
    }

    @Override
    public void deleteById(ID id) {
        CriteriaQuery<T> criteria = (CriteriaQuery<T>) criteriaBuilder.createQuery(t.getClass());
        Root<T> root = (Root<T>) criteria.from(t.getClass());
        criteria.select(root);
        criteria.where(criteriaBuilder.equal(root.get("id"),id));
        T result = entityManager.createQuery(criteria).getSingleResult();
        openSession();
        session.delete(result);
        closeSession();
    }

    /**
     * select * from tbl_ where id=?
     * @param id
     * @return
     */
    @Override
    public Optional<T> findByID(ID id) {
        CriteriaQuery<T> criteria = (CriteriaQuery<T>) criteriaBuilder.createQuery(t.getClass());
        Root<T> root = (Root<T>) criteria.from(t.getClass());
        criteria.select(root);
        criteria.where(criteriaBuilder.equal(root.get("id"),id));
        T result = entityManager.createQuery(criteria).getSingleResult();
        return Optional.ofNullable(result);

//        List<T> result = entityManager.createQuery(criteria).getResultList();
//        if ((result.isEmpty()))//Eğer sorgu neticesinde hiçbir değer dönmez ise boş olarak optional dön
//            return Optional.empty();
//        return Optional.of(result.get(0));// eğer en az bir değer dönecektir. ilk dönen değeri Optional olarak dön
    }

    @Override
    public boolean existById(ID id) {
        CriteriaQuery<T> criteria = (CriteriaQuery<T>) criteriaBuilder.createQuery(t.getClass());
        Root<T> root = (Root<T>) criteria.from(t.getClass());
        criteria.select(root);
        criteria.where(criteriaBuilder.equal(root.get("id"),id));
        T result = entityManager.createQuery(criteria).getSingleResult();
        return result != null; // Eğer kayıt var ise ( null değil ise) -> true | eğer kayit yok ise ( result null ise ) -> false
    }

    /**
     * select * from tbl_
     * @return
     */
    @Override
    public List<T> findAll() {
        CriteriaQuery<T> criteria = (CriteriaQuery<T>) criteriaBuilder.createQuery(t.getClass());
        Root<T> root = (Root<T>) criteria.from(t.getClass());
        criteria.select(root);
        return entityManager.createQuery(criteria).getResultList();
    }


    /**
     * select * from tbl_ where column  = value
     * @param columnName
     * @param value - "%" ve "_" karakterleri kuıllanicilar tarafindan işlenir.
     * @return
     */
    @Override
    public List<T> findByColumnNameAndValue(String columnName, String value) {
        CriteriaQuery<T> criteria = (CriteriaQuery<T>) criteriaBuilder.createQuery(t.getClass());
        Root<T> root = (Root<T>) criteria.from(t.getClass());
        criteria.select(root);
        criteria.where(criteriaBuilder.equal(root.get(columnName),value)); // kullanicinin girdigi kolon adi ve değerin esitligi kontrol edilir.
        criteria.where(criteriaBuilder.like(root.get(columnName),value)); // kullanicinin girdigi kolon adi ve değerin içeriği kontrol edilir.
        return entityManager.createQuery(criteria).getResultList();
    }

    /**
     * Burada yapilmak istenilen detay sudur. Bir sinif icindeki alan adlari( degiskenler) yazilim tarafindan
     * okunulacak ve bu değişlenlerin değerleri  kontrol edilerek null  olmayanlkar sorguya dahil edilecek. Boylefce
     * esnek sorgulama işlemi otomatikleştirilmiş olacak.
     * Bu işlemi yapabilmek içinm Java Reflection Api kullanilacaktir. Bu işlemin adına
     * reverse engineering - Tersine mühendislik olarak da adilandirilabilir.
     *         Musteri musteri = new Musteri();
     *         musteri.setAd("M");
     *         musteri.setSoyad("Hoca");
     * select * from tbl_musteri where ad like '%M%' and soyad like '%HOCA%'
     * @param entity
     * @return
     */
    @Override
    public List<T> findByEntity(T entity) {
        List<T> result =null;
        Class cl = entity.getClass();// entitynin class ozelliklerini geçiyorum
        Field[] fl = cl.getDeclaredFields();//Class icindeki tüm degiskenlkeri bir liste icine aliyorum. id,ad,soyad vs.

        try{
            CriteriaQuery<T> criteria = (CriteriaQuery<T>) criteriaBuilder.createQuery(t.getClass());
            Root<T> root = (Root<T>) criteria.from(t.getClass());
            criteria.select(root);
            List<Predicate> predicateList = new ArrayList<>();// sorgu için gerekli kriterlerin listesini ekleyeceğimiz liste.

            for(int i=0; i<fl.length; i++){
                fl[i].setAccessible(true); // erismek istedigimiz alanlarin erisimini aciyoruz. bunu unutursaniz null gelecektir.
                /**
                 * okumakta oldugum alan null degil ise,
                 * ayrıca okudugum alan id degil ise,
                 *
                 */
                if(fl[i].get(entity.getClass())!=null && !fl[i].get(entity).equals("id")){
                    /**
                     * Sorguları yazarken degiskenlerin tipi önemlidir. mesela int bir deger için like kullanamazsiniz.
                     */
                    if (fl[i].getType().isAssignableFrom(String.class)){
                        predicateList.add(criteriaBuilder.like(root.get(fl[i].getName()),"%"+fl[i].get(entity)+"%"));
                    }
                    else if (fl[i].getType().isAssignableFrom(Number.class)){
                        predicateList.add(criteriaBuilder.equal(root.get(fl[i].getName()),fl[i].get(entity)));
                    }
                    else
                        predicateList.add(criteriaBuilder.equal(root.get(fl[i].getName()),fl[i].get(entity)));
                }
            }

            criteria.where(predicateList.toArray(new Predicate[]{}));//[34, 23452, 242353, 34534, 6]
            result = entityManager.createQuery(criteria).getResultList();

        }catch (Exception e){
            System.out.println("Beklenmeyen bir hata oldu...: " + e.toString());
        }


        return result;
    }
}
