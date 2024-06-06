package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoComments;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    private ItemDto itemDto;
    private ItemDtoComments itemDtoComments;
    private CommentDto commentDto;

    @BeforeEach
    void create() {
        itemDto = new ItemDto(1, "name", "description", true, 1);

        itemDtoComments = new ItemDtoComments(1, "name", "description", true,
                null, null, null);

        commentDto = new CommentDto(1, "comment", "test", LocalDateTime.now());
    }

    @SneakyThrows
    @Test
    void getAll() {
        Integer from = 0;
        Integer size = 10;
        Integer userId = 1;

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(itemService).getItemsOfUser(userId, from, size);
    }

    @SneakyThrows
    @Test
    void getById() {
        Integer itemId = 1;
        Integer userId = 1;
        Mockito.when(itemService.getItemById(Mockito.anyInt(), Mockito.anyInt())).thenReturn(itemDtoComments);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Mockito.verify(itemService).getItemById(itemId, userId);
    }

    @SneakyThrows
    @Test
    void getItems() {
        Integer from = 0;
        Integer size = 20;
        String text = "";

        String result = mockMvc.perform(get("/items/search")
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of()), result);
    }


    @SneakyThrows
    @Test
    void addItem() {
        Mockito.when(itemService.addItem(Mockito.any(), Mockito.anyInt())).thenReturn(itemDto);

        String result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
    }

    @SneakyThrows
    @Test
    void addItemIsNotValid() {
        ItemDto itemDto = new ItemDto(1, "", "description", true,
                1);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
        Mockito.verify(itemService, Mockito.never()).addItem(Mockito.any(), Mockito.anyInt());
    }

    @SneakyThrows
    @Test
    void update() {
        Integer itemId = 1;

        Mockito.when(itemService.updateItem(Mockito.any(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(itemDto);

        String result = mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
    }

    @SneakyThrows
    @Test
    void addComment() {
        Integer id = 1;

        Mockito.when(itemService.addComment(Mockito.anyInt(), Mockito.anyInt(), Mockito.any()))
                .thenReturn(commentDto);

        String result = mockMvc.perform(post("/items/{id}/comment", id)
                        .header("X-Sharer-User-Id", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(commentDto), result);
    }

    @SneakyThrows
    @Test
    void deleteById() {
        Integer itemId = 1;

        mockMvc.perform(delete("/items/{itemId}", itemId))
                .andDo(print())
                .andExpect(status().isOk());

        Mockito.verify(itemService).deleteItem(itemId);
    }
}
