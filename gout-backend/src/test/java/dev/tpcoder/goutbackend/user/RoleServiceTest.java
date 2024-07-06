package dev.tpcoder.goutbackend.user;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.tpcoder.goutbackend.common.enumeration.RoleEnum;
import dev.tpcoder.goutbackend.user.model.Role;
import dev.tpcoder.goutbackend.user.repository.RoleRepository;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @InjectMocks
    private RoleService roleService;

    @Mock
    private RoleRepository roleRepository;

    @Test
    void shouldReturnRoles() {
        var mockRoles = List.of(
                new Role(RoleEnum.CONSUMER.getId(), RoleEnum.CONSUMER.name()),
                new Role(RoleEnum.ADMIN.getId(), RoleEnum.ADMIN.name()),
                new Role(RoleEnum.COMPANY.getId(), RoleEnum.COMPANY.name()));
        when(roleRepository.findAll())
                .thenReturn(mockRoles);

        var actual = roleService.getAllRole();
        List<Role> result = new ArrayList<>();
        actual.iterator().forEachRemaining(result::add);

        Assertions.assertEquals(3, result.size());
    }
}
