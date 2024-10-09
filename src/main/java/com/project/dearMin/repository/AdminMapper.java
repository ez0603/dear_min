package com.project.dearMin.repository;

import com.project.dearMin.entity.account.Admin;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper {
    public int saveAdmin(Admin admin);

    public Admin findAdminByUsername(String username);
}
