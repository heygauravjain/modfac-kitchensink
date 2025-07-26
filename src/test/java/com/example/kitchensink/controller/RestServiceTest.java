package com.example.kitchensink.controller;

import com.example.kitchensink.entity.MemberEntity;
import com.example.kitchensink.repository.MemberRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(RestService.class)
class RestServiceTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private MemberRepository memberRepository;

  private List<MemberEntity> memberEntities;

  @BeforeEach
  void setUp() {
    memberEntities =
        Arrays.asList(
            new MemberEntity("123","John Doe", "john.doe@example.com", "1234567890"),
            new MemberEntity("456","Jane Smith", "jane.smith@example.com", "9876543210"));
  }

  @Test
  void listAllMembers() throws Exception {
    Mockito.when(memberRepository.findAllOrderedByName()).thenReturn(memberEntities);

    mockMvc
        .perform(MockMvcRequestBuilders.get("/rest/members"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));

    Mockito.verify(memberRepository, Mockito.times(1)).findAllOrderedByName();
    Mockito.verifyNoMoreInteractions(memberRepository);
  }
}
