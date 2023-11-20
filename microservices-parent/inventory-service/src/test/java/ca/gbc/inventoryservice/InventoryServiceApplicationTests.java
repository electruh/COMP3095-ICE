package ca.gbc.inventoryservice;

import ca.gbc.inventoryservice.dto.InventoryRequest;
import ca.gbc.inventoryservice.dto.InventoryResponse;
import ca.gbc.inventoryservice.model.Inventory;
import ca.gbc.inventoryservice.repository.InventoryRepository;
import ca.gbc.inventoryservice.service.InventoryServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.web.servlet.MockMvc;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
@SpringBootTest
@AutoConfigureMockMvc
class InventoryServiceApplicationTests extends AbstractContainerBaseTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private InventoryRepository inventoryRepository;

	@Autowired
	private InventoryServiceImpl inventoryService;


	@Test
	void isInStock() throws Exception {
		// Given
		List<Inventory> products = new ArrayList<>();
		products.add(Inventory.builder().skuCode("sku_12345").quantity(1).build());
		products.add(Inventory.builder().skuCode("sku_55555").quantity(2).build());
		products.add(Inventory.builder().skuCode("sku_66666").quantity(0).build());
		inventoryRepository.saveAll(products);

		// When
		List<InventoryRequest> inventoryRequests = getInventoryRequests();
		List<InventoryResponse> responses = inventoryService.isInStock(inventoryRequests);

		// Then
		assertEquals(inventoryRequests.size(), responses.size());

		for (int i = 0; i < inventoryRequests.size(); i++) {
			InventoryRequest request = inventoryRequests.get(i);
			InventoryResponse response = responses.get(i);

			assertEquals(request.getSkuCode(), response.getSkuCode());
		}
	}

	private List<InventoryRequest> getInventoryRequests() {
		List<InventoryRequest> inventoryRequests = new ArrayList<>();
		inventoryRequests.add(InventoryRequest.builder().skuCode("sku_12345").quantity(1).build());
		inventoryRequests.add(InventoryRequest.builder().skuCode("sku_55555").quantity(2).build());

		return inventoryRequests;
	}

}
