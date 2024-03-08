package com.mipt.hsse.tech.rentservice.Controllers;

import com.mipt.hsse.tech.rentservice.DTOs.Requests.*;
import com.mipt.hsse.tech.rentservice.DTOs.Responses.RentInfoResponse;
import com.mipt.hsse.tech.rentservice.DTOs.ShortRentInfo;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/renting")
public class RentController {

  @PostMapping("/rent-item")
  public void rentItem(@RequestBody RentItemRequest request) {
    throw new NotImplementedException();
  }

  @DeleteMapping("/unrent-item/{item_id}")
  public void unrentItem(@PathVariable("item_id") long itemId) {
    throw new NotImplementedException();
  }

  @PatchMapping("/edit-time")
  public void editRentTime(@RequestBody EditRentTimeRequest request) {
    throw new NotImplementedException();
  }

  @GetMapping("/items/{id}")
  public RentInfoResponse getLoadRentInfo(
      @PathVariable("id") long id,
      @RequestParam(value = "loadRentInfo", defaultValue = "false") boolean loadRentInfo) {
    throw new NotImplementedException();
  }

  @PostMapping("/create-item-type")
  public void createItemType(@RequestBody CreateItemTypeRequest request) {
    throw new NotImplementedException();
  }

  @PostMapping("/create-item")
  public void createItem(@RequestBody CreateItemRequest request) {
    throw new NotImplementedException();
  }

  @PostMapping("/{rent_id}/confirm/photo")
  public void pinPhotoConfirmation(
      @PathVariable("rent_id") long rentId, @RequestBody PinPhotoConfirmationRequest request) {
    throw new NotImplementedException();
  }

  @GetMapping("/confirm/photo/{id}")
  public void getPhotoConfirmation(@PathVariable("id") long photoId) {
    throw new NotImplementedException();
  }

  @GetMapping("/items/{item_id}/qr")
  public void getItemBookingQRCode(@PathVariable("item_id") long itemId) {
    throw new NotImplementedException();
  }

  @GetMapping("/{rent_id}")
  public ShortRentInfo getRentInfo(@PathVariable("rent_id") long rentId) {
    throw new NotImplementedException();
  }

  @PatchMapping("/types/{id}")
  public void updateItemType(
      @PathVariable("id") long itemTypeId, @RequestBody UpdateItemTypeRequest request) {
    throw new NotImplementedException();
  }

  @PatchMapping("/items/{id}")
  public void updateItem(@PathVariable String id, @RequestBody UpdateItemRequest request) {
    throw new NotImplementedException();
  }

  @PostMapping("/items/{id}/open")
  public void requestOpenItem(@PathVariable("id") long itemId) {}
}
