package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestService itemRequestService;

    @SneakyThrows
    @Test
    void getItemRequestById() {
        Integer userId = 1;
        Integer requestId = 1;

        ItemRequestDto itemRequestDto = new ItemRequestDto(1, "description", null, null);

        Mockito.when(itemRequestService.getItemRequestById(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(itemRequestDto);

        String result = mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestDto), result);
    }

    @SneakyThrows
    @Test
    void getAllItemRequest() {
        Integer from = 0;
        Integer size = 10;
        Integer userId = 1;

        ItemRequestDto itemRequestDto = new ItemRequestDto(1, "description", null, null);

        Mockito.when(itemRequestService.getAllItemRequest(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(itemRequestDto));

        String result = mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(itemRequestDto)), result);
    }

    @SneakyThrows
    @Test
    void getItemRequestUser() {
        Integer userId = 1;

        ItemRequestDto itemRequestDto = new ItemRequestDto(1, "description", null, null);

        Mockito.when(itemRequestService.getItemRequestUser(Mockito.anyInt()))
                .thenReturn(List.of(itemRequestDto));

        String result = mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(itemRequestDto)), result);
    }

    @SneakyThrows
    @Test
    void create() {
        Integer userId = 1;

        ItemRequestDto itemRequestDto = new ItemRequestDto(1, "description", null, null);

        Mockito.when(itemRequestService.addItemRequest(Mockito.any(), Mockito.anyInt())).thenReturn(itemRequestDto);

        String result = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestDto), result);
    }

    @SneakyThrows
    @Test
    void addItemRequestError() {
        Integer userId = null;

        ItemRequestDto itemRequestDto = new ItemRequestDto(1, "description", null, null);

        Mockito.when(itemRequestService.addItemRequest(Mockito.any(), Mockito.anyInt()))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().is(500));

        Mockito.verify(itemRequestService, Mockito.never()).addItemRequest(itemRequestDto, userId);
    }
}
