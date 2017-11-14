package pers.com.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pers.com.model.Order;

/**
 * Created by chenmutime on 2017/11/14.
 */
@Repository
public interface OrderDao extends JpaRepository<Order, String> {
}
