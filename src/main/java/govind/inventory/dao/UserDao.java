package govind.inventory.dao;

import govind.inventory.dao.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDao {
	List<UserEntity> findAll();
}
