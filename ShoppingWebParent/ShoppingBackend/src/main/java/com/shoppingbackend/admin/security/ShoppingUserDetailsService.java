package com.shoppingbackend.admin.security;

import com.shopping.common.entity.Role;
import com.shopping.common.entity.User;
import com.shoppingbackend.admin.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ShoppingUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Custom lại nó load username là email:
        // get User từ Database bằng email:
        User user = userRepository.findByEmail(email);
            // Chú ý để fetch là EAGER ở field roles của Entity User để tránh error LazyInitializationException
            // -> Dữ liệu về roles của user sẽ được tải ngay lập tức cùng với user (E.g. userRepository.findByEmail(email); thì nó sẽ select join ể lấy dữ liệu từ cả hai bảng User và Role)

        UserDetails userDetails;

        if(user!=null)
        {
            // list roles of user:
            Set<Role> roles = user.getRoles();

            List<SimpleGrantedAuthority> userRoles = new ArrayList<SimpleGrantedAuthority>();
            for(Role role : roles)
            {
                userRoles.add(new SimpleGrantedAuthority(role.getName()));
            }

            // return về UserDetails :
            // UserDetails dùng để lưu các thông tin của User như (getUserName, getPassword, getAuthorities, isAccountNonExpired, isEnabled,...)
            // UserDetails có implementation class mặc định là User
            // Hoặc ta có thể tự custom implementation class cho interface UserDetails :
             userDetails = org.springframework.security.core.userdetails.User
                                            .withUsername(user.getEmail())
                                            .password(user.getPassword())
                                            .authorities(userRoles)
                                            .disabled(!user.isEnabled()) // Vì trong code method .disable() nó sẽ phủ định như này : !this.disable -> do đó ta phải truyền vào kèm dấu !
                                            .build();
            return userDetails;
        }
        else
            throw new UsernameNotFoundException("There is no user with email: "+email);

    }
}
