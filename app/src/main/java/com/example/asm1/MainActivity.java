package com.example.asm1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.asm1.Adapter.TableListAdapter;
import com.example.asm1.Api.ApiService;
import com.example.asm1.Model.ProductModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,TableListAdapter.Callback {
    private RecyclerView rcvProduct;
    private TableListAdapter adapter;
    private FloatingActionButton fab;
    private List<ProductModel> mList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mList = new ArrayList<>();
        rcvProduct = (RecyclerView) findViewById(R.id.rcv_table);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvProduct.setLayoutManager(linearLayoutManager);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcvProduct.addItemDecoration(itemDecoration);

        adapter = new TableListAdapter(new ArrayList<>(), this);
        rcvProduct.setAdapter(adapter);
        callApiGetTableList();
    }


    private void callApiGetTableList() {
        // lay danh sach
        ApiService.apiService.getProduct().enqueue(new Callback<List<ProductModel>>() {
            @Override
            public void onResponse(Call<List<ProductModel>> call, Response<List<ProductModel>> response) {
                if (response.isSuccessful()) {
                    List<ProductModel> tableItems = response.body();
                    if (tableItems != null) {
                        adapter.setTableItems(tableItems);
                        adapter.notifyDataSetChanged();
                        rcvProduct.setAdapter(adapter);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Failed to retrieve table list", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ProductModel>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Network error" + t, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_product, null);
        builder.setView(dialogView);

        final EditText edtName = dialogView.findViewById(R.id.edt_name);
        final EditText edtPrice = dialogView.findViewById(R.id.edt_price);
        final EditText edtQuantity = dialogView.findViewById(R.id.edt_quantity);
        final Button btnAdd = dialogView.findViewById(R.id.btn_add);
        final Button btnCanel = dialogView.findViewById(R.id.btn_canel);


        final AlertDialog dialog = builder.create();
        dialog.show();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString();
                int price = Integer.valueOf(edtPrice.getText().toString());
                int quantity = Integer.valueOf(edtQuantity.getText().toString());
                addNewData(name, price, quantity);
                dialog.dismiss();
            }
        });
        btnCanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                dialog.dismiss();
            }
        });

    }

    private void addNewData(String name, Integer price, Integer quantity) {
        ProductModel product = new ProductModel();
        product.setName(name);
        product.setPrice(price);
        product.setQuantity(quantity);
        ApiService.apiService.addCar(product).enqueue(new Callback<ProductModel>() {
            @Override
            public void onResponse(Call<ProductModel> call, Response<ProductModel> response) {
                if (response.isSuccessful()) {
                    // Xử lý thành công
                    Toast.makeText(MainActivity.this, "Thêm dữ liệu thành công", Toast.LENGTH_SHORT).show();
                } else {
                    // Xử lý lỗi khi thêm dữ liệu
                    Toast.makeText(MainActivity.this, "Lỗi khi thêm dữ liệu", Toast.LENGTH_SHORT).show();
                }
                callApiGetTableList();
            }

            @Override
            public void onFailure(Call<ProductModel> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCar(String id, String name, int price, int quantity) {
        ProductModel productModel = new ProductModel();
        productModel.setName(name);
        productModel.setPrice(price);
        productModel.setQuantity(quantity);

        Call<ProductModel> call = ApiService.apiService.updateCar(id, productModel);
        call.enqueue(new Callback<ProductModel>() {
            @Override
            public void onResponse(Call<ProductModel> call, Response<ProductModel> response) {
                if (response.isSuccessful()) {
                    ProductModel updatedProduct = response.body();
                    Toast.makeText(MainActivity.this, "Sửa thành công", Toast.LENGTH_SHORT).show();
                    callApiGetTableList();
                } else {
                    Log.d("MAIN", "Respone Fail" + response.message());
                }
            }

            @Override
            public void onFailure(Call<ProductModel> call, Throwable t) {
                Log.d("MAIN", "Respone Fail" + t.getMessage());
            }
        });
    }

    @Override
    public void editPr(ProductModel model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_product, null);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.show();

        EditText edName = dialog.findViewById(R.id.edt_name);
        EditText edPrice = dialog.findViewById(R.id.edt_price);
        EditText edQuantity = dialog.findViewById(R.id.edt_quantity);
        Button btnEdit = dialog.findViewById(R.id.btn_add);
        Button btnCancel = dialog.findViewById(R.id.btn_canel);

        btnEdit.setText("Sửa");
        edName.setText(model.getName());
        edPrice.setText(String.valueOf(model.getPrice()));
        edQuantity.setText(String.valueOf(model.getQuantity()));

        btnEdit.setOnClickListener(v -> {
            String name = edName.getText().toString().trim();
            int price = Integer.parseInt(edPrice.getText().toString().trim());
            int quantity = Integer.parseInt(edQuantity.getText().toString().trim());

            updateCar(model.getId(), name, price, quantity);
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    @Override
    public void deletePr(ProductModel model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xóa sản phẩm");
        builder.setMessage("Bạn có chắc chắn muốn xóa sản phẩm này?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            deleteProduct(model);
        });
        builder.setNegativeButton("Hủy", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void deleteProduct(ProductModel model) {
        String id = model.getId();
        Call<Void> call = ApiService.apiService.deleteCars(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                    callApiGetTableList();
                } else {
                    Log.d("MAIN", "Respone Fail" + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("MAIN", "Respone Fail" + t.getMessage());
            }
        });
    }

}