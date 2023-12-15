package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mvc;
    private ItemRequestDto itemRequestDto = new ItemRequestDto(1L,
            "Ищу синего мишку из СССР", null, List.of(new ItemShortDto(1L, "Гитара",
            "Cемиструнная", true, 10L, null)));

    @Test
    public void addRequest() throws Exception {
        ItemRequestShortDto requestShortDto = new ItemRequestShortDto(1L, "Ищу синего мишку из СССР",
                LocalDateTime.now());
        when(itemRequestService.addRequest(anyLong(), any()))
                .thenReturn(requestShortDto);

        mvc.perform(MockMvcRequestBuilders.post("/requests")
                        .header("X-Sharer-User-Id", 10L)
                        .content(mapper.writeValueAsString(requestShortDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestShortDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestShortDto.getDescription())));
    }

    @Test
    public void getItemRequest() throws Exception {
        when(itemRequestService.getItemRequest(anyLong(), anyLong()))
                .thenReturn(itemRequestDto);

        mvc.perform(MockMvcRequestBuilders.get("/requests/1")
                        .header("X-Sharer-User-Id", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.items[0].id", is(itemRequestDto.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$.items[0].name", is(itemRequestDto.getItems().get(0).getName())))
                .andExpect(jsonPath("$.items[0].description", is(itemRequestDto.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$.items[0].available", is(itemRequestDto.getItems().get(0).getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.items[0].ownerId", is(itemRequestDto.getItems().get(0).getOwnerId()), Long.class));
    }

    @Test
    public void getUserItemRequests() throws Exception {
        when(itemRequestService.getUserItemRequests(anyLong()))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(MockMvcRequestBuilders.get("/requests")
                        .header("X-Sharer-User-Id", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].items[0].id", is(itemRequestDto.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name", is(itemRequestDto.getItems().get(0).getName())))
                .andExpect(jsonPath("$[0].items[0].description", is(itemRequestDto.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$[0].items[0].available", is(itemRequestDto.getItems().get(0).getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].items[0].ownerId", is(itemRequestDto.getItems().get(0).getOwnerId()), Long.class));
    }

    @Test
    public void getOtherItemRequestsWithoutParametrs() throws Exception {
        when(itemRequestService.getOtherItemRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header("X-Sharer-User-Id", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].items[0].id", is(itemRequestDto.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name", is(itemRequestDto.getItems().get(0).getName())))
                .andExpect(jsonPath("$[0].items[0].description", is(itemRequestDto.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$[0].items[0].available", is(itemRequestDto.getItems().get(0).getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].items[0].ownerId", is(itemRequestDto.getItems().get(0).getOwnerId()), Long.class));
    }

    @Test
    public void getOtherItemRequestsWithParametrs() throws Exception {
        when(itemRequestService.getOtherItemRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(MockMvcRequestBuilders.get("/requests/all?from=0&size=2")
                        .header("X-Sharer-User-Id", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].items[0].id", is(itemRequestDto.getItems().get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name", is(itemRequestDto.getItems().get(0).getName())))
                .andExpect(jsonPath("$[0].items[0].description", is(itemRequestDto.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$[0].items[0].available", is(itemRequestDto.getItems().get(0).getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].items[0].ownerId", is(itemRequestDto.getItems().get(0).getOwnerId()), Long.class));
    }
}
