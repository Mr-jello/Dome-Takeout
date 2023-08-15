package top.mrjello.service;


import top.mrjello.entity.AddressBook;

import java.util.List;

public interface AddressBookService {

    /**
     * 条件查询
     * @param addressBook 条件
     * @return 地址列表
     */
    List<AddressBook> list(AddressBook addressBook);

    /**
     * 新增
     * @param addressBook 地址
     */
    void save(AddressBook addressBook);

    /**
     * 根据id查询
     * @param id id
     * @return 地址
     */
    AddressBook getById(Long id);

    /**
     * 根据id修改
     * @param addressBook 地址
     */
    void update(AddressBook addressBook);

    /**
     * 设置默认地址
     * @param addressBook 地址
     */
    void setDefault(AddressBook addressBook);

    /**
     * 根据id删除地址
     * @param id id
     */
    void deleteById(Long id);

}
