package com.admin.common.configuration.security;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.annotation.Jsr250Voter;
import org.springframework.security.access.expression.method.ExpressionBasedPreInvocationAdvice;
import org.springframework.security.access.prepost.PreInvocationAuthorizationAdviceVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 为了增加 自定义投票者
 */
public class MyGlobalMethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {

    private AnnotationAttributes enableMethodSecurity;

    private BeanFactory context;

    public MyGlobalMethodSecurityConfiguration(BeanFactory context) {
        this.context = context;
    }

    @Override
    protected AccessDecisionManager accessDecisionManager() {
        List<AccessDecisionVoter<?>> decisionVoters = new ArrayList<>();

        // 在源码基础上，增加 ↓
        decisionVoters.add(new MyAccessDecisionVoter()); // 自定义投票者
        // 在源码基础上，增加 ↑

        if (prePostEnabled()) {
            ExpressionBasedPreInvocationAdvice expressionAdvice = new ExpressionBasedPreInvocationAdvice();
            expressionAdvice.setExpressionHandler(getExpressionHandler());
            decisionVoters.add(new PreInvocationAuthorizationAdviceVoter(expressionAdvice));
        }
        if (jsr250Enabled()) {
            decisionVoters.add(new Jsr250Voter());
        }
        RoleVoter roleVoter = new RoleVoter();
        GrantedAuthorityDefaults grantedAuthorityDefaults = getSingleBeanOrNull(GrantedAuthorityDefaults.class);
        if (grantedAuthorityDefaults != null) {
            roleVoter.setRolePrefix(grantedAuthorityDefaults.getRolePrefix());
        }
        decisionVoters.add(roleVoter);
        decisionVoters.add(new AuthenticatedVoter());
        return new AffirmativeBased(decisionVoters);
    }

    private <T> T getSingleBeanOrNull(Class<T> type) {
        try {
            return this.context.getBean(type);
        } catch (NoSuchBeanDefinitionException ex) {
        }
        return null;
    }

    private boolean prePostEnabled() {
        return enableMethodSecurity().getBoolean("prePostEnabled");
    }

    private boolean jsr250Enabled() {
        return enableMethodSecurity().getBoolean("jsr250Enabled");
    }

    private AnnotationAttributes enableMethodSecurity() {
        if (this.enableMethodSecurity == null) {
            // if it is null look at this instance (i.e. a subclass was used)
            EnableGlobalMethodSecurity methodSecurityAnnotation =
                AnnotationUtils.findAnnotation(getClass(), EnableGlobalMethodSecurity.class);
            Assert.notNull(methodSecurityAnnotation, () -> EnableGlobalMethodSecurity.class.getName() + " is required");
            Map<String, Object> methodSecurityAttrs = AnnotationUtils.getAnnotationAttributes(methodSecurityAnnotation);
            this.enableMethodSecurity = AnnotationAttributes.fromMap(methodSecurityAttrs);
        }
        return this.enableMethodSecurity;
    }

}
