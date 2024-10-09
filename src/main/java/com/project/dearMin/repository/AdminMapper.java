package com.project.dearMin.repository;

import com.project.dearMin.entity.account.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminMapper {
    public int saveAdmin(Admin admin);

    public Admin findAdminByUsername(String username);

    public int modifyPassword(Admin admin);

    public Admin findAccountByNameAndEmail(@Param("adminName") String adminName, @Param("email") String email);

    public Admin findAccountByUserNameAndEmail(@Param("username") String username, @Param("email") String email);

}
