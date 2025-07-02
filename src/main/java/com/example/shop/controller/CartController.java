package com.example.shop.controller;

import com.example.shop.dto.CartDetailDto;
import com.example.shop.dto.CartItemDto;
import com.example.shop.entity.Cart;
import com.example.shop.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;

    @PostMapping(value = "/cart")
    public @ResponseBody ResponseEntity<?> order(@RequestBody
         @Valid CartItemDto cartItemDto, BindingResult bindingResult,
                                                 Principal principal) {
         if (bindingResult.hasErrors()) {
             StringBuilder sb = new StringBuilder();
             List<FieldError> fieldErrors = bindingResult.getFieldErrors();
             for (FieldError fieldError : fieldErrors) {
                 sb.append(fieldError.getDefaultMessage());
             }
             return new ResponseEntity<>(sb.toString(), HttpStatus.BAD_REQUEST);
         }

         String email = principal.getName();
         Long cartItemId;

         try{
             cartItemId = cartService.addCart(cartItemDto, email);
             log.info("cartItemId : {}", cartItemId);

        }catch (Exception e){
             return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
         }

         return new ResponseEntity<Long>(cartItemId, HttpStatus.CREATED);
    }   // end order

    @GetMapping(value = "/cart")
    public String orderHist(Principal principal, Model model) {
        List<CartDetailDto> cartDetailList = cartService.getCartList(principal.getName());

        model.addAttribute("cartItems", cartDetailList);

        return "cart/cartList";
    }
}
