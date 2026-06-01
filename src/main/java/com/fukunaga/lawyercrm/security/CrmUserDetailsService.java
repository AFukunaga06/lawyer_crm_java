package com.fukunaga.lawyercrm.security;

import com.fukunaga.lawyercrm.entity.User;
import com.fukunaga.lawyercrm.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * login_id でユーザーを引く。PHP版は (tenant_id, login_id) で一意だが、
 * 単一フォームのログインでは全テナント横断の login_id 一意性を前提に検索する。
 * （初期構成では login_id をシステム全体で一意に運用する。）
 */
@Service
public class CrmUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CrmUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません: " + loginId));
        return new CrmUserDetails(user);
    }
}
