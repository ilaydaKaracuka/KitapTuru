package com.example.myapplication6;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication6.Kitap;
import com.example.myapplication6.R;
import java.util.ArrayList;
public class KitapAdapter extends RecyclerView.Adapter<KitapAdapter.KitapHolder> {
    private ArrayList<Kitap> kitapList;
    private Context context;
    private OnItemClickListener listener;

    public KitapAdapter(ArrayList<Kitap> kitapList, Context context) {
        this.kitapList = kitapList;
        this.context = context;
    }

    @NonNull
    @Override
    public KitapHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.kitap_item, parent, false); // kitap_item dosyasındaki öğelerin View nesnelerine dönüştürülmesini sağlar. 1.parametre=her kitabın görüntüleneceği xml dosyası. 2.parametre=bu viewın hangi viewgroup içine ekleneceğini belirtir 3.parametre=view hemen parent'a eklenmez recyclerview sonradan yerleştirir
        return new KitapHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull KitapHolder holder, int position) {
        Kitap kitap = kitapList.get(position); //Listeden belirtilen pozisyondaki kitabı alır.
        holder.setData(kitap);                                                                                                                        //Her bir kitaba ait bilgiyi (ad, yazar, vb.) uygun view'a yerleştirir .

    }

    @Override
    public int getItemCount() {
        return kitapList.size();
    }

    class KitapHolder extends RecyclerView.ViewHolder{
        TextView txtKitapAdi, txtKitapYazari, txtKitapYorumu;
        ImageView imgKitapResim;

        public KitapHolder(@NonNull View itemView) {
            super(itemView);

            txtKitapAdi = (TextView)itemView.findViewById(R.id.kitap_item_textViewKitapAdi);
            txtKitapYazari = (TextView)itemView.findViewById(R.id.kitap_item_textViewKitapYazari);
            txtKitapYorumu = (TextView)itemView.findViewById(R.id.kitap_item_textViewKitapYorumu);
            imgKitapResim = (ImageView) itemView.findViewById(R.id.kitap_item_imageViewKitapResim);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition(); //tıklanan öğenin pozisyonu

                    if (listener != null && position != RecyclerView.NO_POSITION)
                        listener.onItemClick(kitapList.get(position)); // Tıklanan kitap nesnesini gönderir.
                }
            });
        }

        public void setData(Kitap kitap){
            this.txtKitapAdi.setText(kitap.getKitapAdi());
            this.txtKitapYazari.setText(kitap.getKitapYazari());
            this.txtKitapYorumu.setText(kitap.getKitapYorumu());
            this.imgKitapResim.setImageBitmap(kitap.getKitapResim());
        }
    }

    public interface OnItemClickListener{                                                                                                         //Kitap nesnesi tıklanıldığında yapılması gereken işlemi dışarıya bildirir
        void onItemClick(Kitap kitap); // Tıklanan kitap nesnesi parametre olarak iletilir.
    }

    public void setOnItemClickListener(OnItemClickListener listener){ //dışarıdan bir OnItemClickListener nesnesi alır ve bunu sınıfın içinde tanımlı olan listener değişkenine atar.
        this.listener = listener;
    }
    public void updateList(ArrayList<Kitap> yeniListe) {
        this.kitapList = yeniListe;
        notifyDataSetChanged();
    }

}
