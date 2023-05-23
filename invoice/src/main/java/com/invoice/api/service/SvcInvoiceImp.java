package com.invoice.api.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.invoice.api.dto.ApiResponse;
import com.invoice.api.dto.DtoProduct;
import com.invoice.api.entity.Cart;
import com.invoice.api.entity.Invoice;
import com.invoice.api.entity.Item;
import com.invoice.api.repository.RepoCart;
import com.invoice.api.repository.RepoInvoice;
import com.invoice.api.repository.RepoItem;
import com.invoice.configuration.client.ProductClient;
import com.invoice.exception.ApiException;

@Service
public class SvcInvoiceImp implements SvcInvoice {

	@Autowired
	RepoInvoice repo;
	
	@Autowired
	RepoItem repoItem;

	@Autowired
	RepoCart repoCart;

	@Autowired
	ProductClient productCl;

	@Override
	public List<Invoice> getInvoices(String rfc) {
		return repo.findByRfcAndStatus(rfc, 1);
	}

	@Override
	public List<Item> getInvoiceItems(Integer invoice_id) {
		return repoItem.getInvoiceItems(invoice_id);
	}

	@Override
	public ApiResponse generateInvoice(String rfc) {
		/*
		 * Requerimiento 5
		 * Implementar el método para generar una factura 
		 */
		//Obtenemos el carrito del cliente
		List<Cart> clientCart = repoCart.findByRfcAndStatus(rfc, 1);
		if(clientCart.isEmpty())
			throw new ApiException(HttpStatus.NOT_FOUND, "cart has no items");
		
		List<Item> itemList = new ArrayList<>();
		Double invoiceTotal = 0.0;
		Double invoiceTaxes = 0.0;
		Double invoiceSubtotal = 0.0;
		for(Cart cart : clientCart){
			DtoProduct product;
			try {
				ResponseEntity<DtoProduct> response = productCl.getProduct(cart.getGtin());
				if(response.getStatusCode() == HttpStatus.OK)
					product = response.getBody();
				else
					product = null;
			} catch(Exception e) {
				throw new ApiException(HttpStatus.BAD_REQUEST, "unable to retrieve product information");
			}
			//Por cada producto de cada carrito creamos un item y vamos sumando al invoice
			if(product != null){
				Double unitPrice = product.getPrice();
				Double total = cart.getQuantity() * unitPrice;
				Double taxes = total * 0.16;
				Double subtotal = total - taxes;
				Item newItem = new Item(cart.getGtin(), cart.getQuantity(), unitPrice, subtotal, taxes, total, 1);
				itemList.add(newItem);
				invoiceTotal += total;
				invoiceTaxes += taxes;
				invoiceSubtotal += subtotal;
				//Actualizamos el stock del producto (Recordar que el método le quita la cantidad que le pasas)
				productCl.updateProductStock(product.getGtin(), cart.getQuantity());
			}
			//Desactivamos el carrito
			cart.setStatus(0);
		}
		//Guardamos invoice
		Invoice newInvoice = new Invoice(rfc, invoiceSubtotal,invoiceTaxes,invoiceTotal, LocalDateTime.now(),1);
		Integer invoiceId = repo.save(newInvoice).getInvoice_id();
		//Guardamos items
		for(Item item : itemList){
			item.setId_invoice(invoiceId);
			repoItem.save(item);
		}

		return new ApiResponse("invoice generated");
	}

}
