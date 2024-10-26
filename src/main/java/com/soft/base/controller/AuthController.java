package com.soft.base.controller;

import com.soft.base.entity.SysUser;
import com.soft.base.request.LoginRequest;
import com.soft.base.request.RegisterRequest;
import com.soft.base.service.AuthService;
import com.soft.base.utils.AESUtil;
import com.soft.base.utils.SecurityUtil;
import com.soft.base.vo.LoginVo;
import com.soft.base.resultapi.R;
import com.soft.base.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

import static com.soft.base.constants.RegexConstant.USERNAME_PATTERN;
import static com.soft.base.constants.TokenConstant.TOKEN_PREFIX;
import static com.soft.base.enums.ResultEnum.*;

@RestController
@Tag(name = "鉴权")
@RequestMapping(value = "/auth")
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final AuthService authService;
    private final AESUtil aesUtil;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          UserDetailsService userDetailsService,
                          AuthService authService,
                          AESUtil aesUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.authService = authService;
        this.aesUtil = aesUtil;
    }

    @PostMapping("/login")
    @Operation(summary = "登录")
    public R<LoginVo> authenticate(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername()
                    , aesUtil.decrypt(request.getPassword())));
            LoginVo loginVo = new LoginVo();
            loginVo.setToken(TOKEN_PREFIX + jwtUtil.generateToken(request.getUsername()));
            loginVo.setUsername(request.getUsername());
            return R.ok(loginVo);
        } catch (BadCredentialsException e) {
            log.error(e.getMessage(), e);
            return R.fail("账号或密码错误");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return R.fail();
        }
    }

    @PostMapping(value = "/register")
    @Operation(summary = "注册")
    public R register(@RequestBody RegisterRequest request) {
        if (StringUtils.isBlank(request.getUsername())) {
            return R.fail("用户名不能为空");
        }
        if (!Pattern.matches(USERNAME_PATTERN, request.getUsername())) {
            return R.fail("用户名不能使用中文");
        }
        if (StringUtils.isBlank(request.getPassword())) {
            return R.fail("密码不能为空");
        }

        try {
            SysUser sysUser = new SysUser();
            sysUser.setUsername(request.getUsername());
            sysUser.setPassword(request.getPassword());
            sysUser.setNickname(request.getNickname());
            authService.register(sysUser);
            return R.ok("注册成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail();
        }
    }
}
