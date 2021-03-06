package es.unizar.eina.notepadv3;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class Test {

    private NotesDbAdapter mDbHelper;
    private CategoriesDbAdapter mDbHelperCat;

    public Test(NotesDbAdapter mDbHelper, CategoriesDbAdapter mDbHelperCat) {
        this.mDbHelper = mDbHelper;
        mDbHelper.open();
        this.mDbHelperCat = mDbHelperCat;
        mDbHelperCat.open();
    }

    public void testSistema(){
        pruebaCrearNota();
        pruebaCrearCategoria();
        pruebaBorrarNota();
        pruebaBorrarCategoria();
        pruebaEditarNota();
        pruebaEditarCategoria();
    }

    public void testVolumen() throws InterruptedException {
        pruebaVolumenNotas();
        pruebaVolumenCategorias();
    }

    public void testSobrecarga() throws InterruptedException {
        pruebaSobrecargaTituloNotas();
        pruebaSobrecargaTituloCategorias();
        pruebaSobrecargaBodyNotas();
    }

    public void pruebaVolumenNotas() throws InterruptedException {
        int i = 0;
        try{
            mDbHelper.resetNotes();
            for(i = 0; i <= 50000; i++){
                long n = mDbHelper.createNote("Título"+i, "Body", (long)i, "none");

                if(i == 100 || i == 500 || i == 999 || i == 1000 || i == 50000){
                    Cursor c = mDbHelper.fetchNote(n);
                    if(c != null){
                        Log.d("Tag", i+ " notas insertadas correctamente");
                    }
                }
            }
            mDbHelper.resetNotes();

        }catch(Exception e){
            Log.d("Tag", "Error con i = " +i);
        }
    }

    public void pruebaVolumenCategorias() throws InterruptedException {
        int i = 0;
        try{
            mDbHelperCat.resetCategories();
            for(i = 0; i <= 50000; i++){
                long n = mDbHelperCat.createCategory("nombreCat"+i, (long)i);
                if(i == 100 || i == 500 || i == 999 || i == 1000 || i == 50000){
                    Cursor c = mDbHelperCat.fetchCategory(n);
                    if(c != null){
                        Log.d("Tag", i+ " categorías insertadas correctamente");
                    }
                }
            }
            Log.d("Tag", "Exito con i = " +i);
            mDbHelperCat.resetCategories();
        }catch(Exception e){
            Log.d("Tag", "Error con i = " +i);
        }
    }

    public void pruebaSobrecargaBodyNotas() throws InterruptedException{
        int i = 10;
        String bodyNota= "Ho";
        int letras = 0;
        try{
            for(i = 10; i < 1000; i++){
                mDbHelper.resetNotes();
                bodyNota = bodyNota + bodyNota;
                letras = bodyNota.length();
                long n = mDbHelper.createNote("hola",bodyNota,(long) i,"none");
                Cursor c = mDbHelper.fetchNote(n);
                if(c == null){
                    System.out.println("Nota no insertada");
                    break;
                }else{
                    String idNota = c.getString(
                            c.getColumnIndexOrThrow(NotesDbAdapter.KEY_ROWID));
                    Log.d("Tag", "Body nota insertado bien con i = " +idNota +" num de letras = " +letras);
                }

            }
            Log.d("Tag", "Exito con i = " +(i-10) +" num de letras = " +letras);
            mDbHelper.resetNotes();

        }catch(Exception e){
            Log.d("Tag", "Error con i = " +(i-10) +" num de letras = " +letras);
        }
    }

    public void pruebaSobrecargaTituloNotas() throws InterruptedException{
        int i = 10;
        String bodyNota= "Ho";
        int letras = 0;
        try{
            for(i = 10; i < 1000; i++){
                mDbHelper.resetNotes();
                bodyNota = bodyNota + bodyNota;
                letras = bodyNota.length();
                long n = mDbHelper.createNote(bodyNota,"body",(long) i,"none");
                Cursor c = mDbHelper.fetchNote(n);
                if(c == null){
                    System.out.println("Nota no insertada");
                    break;
                }else{
                    String idNota = c.getString(
                            c.getColumnIndexOrThrow(NotesDbAdapter.KEY_ROWID));
                    Log.d("Tag", "Título nota insertado bien con i = " +idNota +" num de letras = " +letras);
                }
            }
            Log.d("Tag", "Exito con i = " +(i-10) +" num de letras = " +letras);
            mDbHelper.resetNotes();

        }catch(Exception e){
            Log.d("Tag", "Error con i = " +(i-10) +" num de letras = " +letras);
        }
    }

    public void pruebaSobrecargaTituloCategorias() throws InterruptedException{
        int i = 10;
        String nombreCat= "Ho";
        int letras = 0;
        try{

            for(i = 10; i < 1000; i++){
                mDbHelperCat.resetCategories();
                nombreCat = nombreCat + nombreCat;
                letras = nombreCat.length();
                long n = mDbHelperCat.createCategory(nombreCat, (long)i);
                Cursor c = mDbHelperCat.fetchCategory(n);
                if(c == null){
                    System.out.println("Categoría no insertada");
                    break;
                }else{
                    String idCat = c.getString(
                            c.getColumnIndexOrThrow(NotesDbAdapter.KEY_ROWID));
                    Log.d("Tag", "Título categoría insertado bien con i = " +idCat +" num de letras = " +letras);
                }
            }
            Log.d("Tag", "Exito con i = " +(i-10) +" num de letras = " +letras);
            mDbHelperCat.resetCategories();

        }catch(Exception e){
            Log.d("Tag", "Error con i = " +(i-10) +" num de letras = " +letras);
        }
    }

    public void pruebaCrearNota(){
        try{
            mDbHelper.resetNotes();
            //Crea notas normales
            for(int i = 0; i<10; i++){
                long n = mDbHelper.createNote("Nota","body",(long) i,"none");
                Cursor c = mDbHelper.fetchNote(n);
                if(c == null){
                    System.out.println("Error al crear nota.");
                }
                if(i == 9 && c != null){
                    System.out.println("Notas normales creadas con éxito.");
                }
            }
        }catch(Exception e){
            Log.d("Tag", "Error en la prueba de creación de notas");
        }
            mDbHelper.resetNotes();
        try{
            //Crea nota con título en blanco
            long n = mDbHelper.createNote("","body",(long) 0,"none");
            Cursor c = mDbHelper.fetchNote(n);
            if(c == null){
                System.out.println("Error al crear nota con título en blanco.");
            }else{
                System.out.println("Éxito al crear nota con título en blanco.");
            }
            mDbHelper.resetNotes();
        }catch(Throwable t){
            Log.d("Tag", "Error al crear nota con título en blanco.");
        }
        try{
            // Crea nota con título nulo
            mDbHelper.createNote(null,"body",(long) 0,"none");
            android.util.Log.d("Tag", "Éxito al crear nota con título nulo.");
        }catch(SQLiteConstraintException t){
            android.util.Log.d("Tag", "Error al crear nota con título nulo.");
        }
        mDbHelper.resetNotes();
        try{
            // Crea nota con body nulo
            mDbHelper.createNote("nota",null,(long) 0,"none");
            System.out.println("Éxito al crear nota con body nulo.");
        }catch(Exception e){
            Log.d("Tag", "Éxito al crear nota con body nulo.");
        }
            mDbHelper.resetNotes();
        try{
            // Crea nota con categoría nula
            mDbHelper.createNote("nota","body",(long) 0,null);
            System.out.println("Éxito al crear nota con categoría nula.");

        }catch(Exception e){
            Log.d("Tag", "Error al crear nota con categoría nula.");
        }
        mDbHelper.resetNotes();
    }

    public void pruebaCrearCategoria(){
        try{
            mDbHelperCat.resetCategories();
            //Crea categorías normales
            for(int i = 0; i<10; i++){
                long n = mDbHelperCat.createCategory("Nota"+i,(long) i);
                Cursor c = mDbHelperCat.fetchCategory(n);
                if(c == null){
                    System.out.println("Error al crear categoría.");
                }
                if(i == 9 && c != null){
                    System.out.println("Categorías normales creadas con éxito.");
                }
            }
        }catch(Exception e){
            Log.d("Tag", "Error en la prueba de creación de categorías");
        }
            mDbHelperCat.resetCategories();
        try{
            //Crea categoría con título en blanco
            mDbHelperCat.createCategory("",(long) 0);
            System.out.println("Éxito al crear categoría con título en blanco.");
        }catch(Exception e){
            Log.d("Tag", "Error al crear categoría con título en blanco.");
        }
            mDbHelperCat.resetCategories();
        try{
            // Crea categoría con título nulo
            mDbHelperCat.createCategory(null,(long) 0);
                System.out.println("Éxito al crear categoría con título nulo.");
        }catch(Exception e){
            Log.d("Tag", "Error al crear categoría con título nulo.");
        }
            mDbHelperCat.resetCategories();
    }

    public void pruebaBorrarNota(){
        try {
            mDbHelper.resetNotes();
            long n = mDbHelper.createNote("Nota","body", (long) 1,"none");
            long n0 = mDbHelper.createNote("Nota","body", (long) 0,"none");
            if(mDbHelper.deleteNote(n)){
                Log.d("Tag", "Nota borrada correctamente con id " +n);
            }else{
                Log.d("Tag", "Nota no borrada con id " +n);
            }
            if(mDbHelper.deleteNote(0)){
                Log.d("Tag", "Nota borrada correctamente con id 0");
            }else{
                Log.d("Tag", "Nota no borrada con id 0");
            }
            if(mDbHelper.deleteNote(-1)){
                Log.d("Tag", "Nota borrada correctamente con id -1");
            }else{
                Log.d("Tag", "Nota no borrada con id -1");
            }
            mDbHelper.resetNotes();
        }catch (Exception e){
            Log.d("Tag", "Error en la prueba de eliminación de notas");
        }

    }

    public void pruebaBorrarCategoria(){
        try {
            mDbHelperCat.resetCategories();
            long n = mDbHelperCat.createCategory("Cat",(long) 1);
            long n0 = mDbHelperCat.createCategory("Cat0",(long) 0);
            if(mDbHelperCat.deleteCategory(n)){
                Log.d("Tag", "Categoría borrada correctamente con id " +n);
            }else{
                Log.d("Tag", "Categoría no borrada con id " +n);
            }
            if(mDbHelperCat.deleteCategory((long)0)){
                Log.d("Tag", "Categoría borrada correctamente con id 0");
            }else{
                Log.d("Tag", "Categoría no borrada con id 0");
            }
            if(mDbHelperCat.deleteCategory((long)-1)){
                Log.d("Tag", "Categoría borrada correctamente con id -1");
            }else{
                Log.d("Tag", "Categoría no borrada con id -1");
            }
            mDbHelperCat.resetCategories();
        }catch (Exception e){
            Log.d("Tag", "Error en la prueba de eliminación de categorías");
        }
    }

    public void pruebaEditarNota(){
        mDbHelper.resetNotes();
        long n = mDbHelper.createNote("Nota","body",(long) 1,"none");
        if(mDbHelper.updateNote((long) 1,"Notaa","bodyy","none")){
            Log.d("Tag", "Nota editada correctamente con id "+n);
        }else{
            Log.d("Tag", "Nota no editada con id "+n);
        }
        if(mDbHelper.updateNote((long) 0,"Notaa","bodyy","none")){
            Log.d("Tag", "Nota editada correctamente con id 0");
        }else{
            Log.d("Tag", "Nota no editada con id 0");
        }
        if(mDbHelper.updateNote((long) -1,"Notaa","bodyy","none")){
            Log.d("Tag", "Nota editada correctamente con id -1");
        }else{
            Log.d("Tag", "Nota no editada con id -1");
        }
        try{
            if(mDbHelper.updateNote((long) n,null,"bodyy","none")){
                Log.d("Tag", "Nota editada correctamente con id "+n + "título nulo");
            }
        }catch(Exception e){
            Log.d("Tag", "Nota no editada con id "+n + " título nulo");
        }
        if(mDbHelper.updateNote((long) n,"","bodyy","none")){
            Log.d("Tag", "Nota editada correctamente con id "+n + " título vacío");
        }else{
            Log.d("Tag", "Nota no editada con id "+n + " título vacío");
        }
        try{
            if(mDbHelper.updateNote((long) n,"Notaa",null,"none")) {
                Log.d("Tag", "Nota editada correctamente con id " + n + " body nulo");
            }
        }catch(Exception e){
            Log.d("Tag", "Nota no editada con id "+n + " body nulo");
        }
        try{
            if(mDbHelper.updateNote((long) n,"Notaa","bodyy",null)){
                Log.d("Tag", "Nota editada correctamente con id "+n + " categoría nula");
            }
        }catch(Exception e){
            Log.d("Tag", "Nota no editada con id "+n + " categoría nula");
        }
        mDbHelper.resetNotes();

    }

    public void pruebaEditarCategoria(){
        mDbHelperCat.resetCategories();
        long n = mDbHelperCat.createCategory("Categoría",(long) 1);
        if(mDbHelperCat.updateCategory((long) 1,"Categoríaa")){
            Log.d("Tag", "Categoría editada correctamente con id "+n);
        }else{
            Log.d("Tag", "Categoría no editada con id "+n);
        }
        if(mDbHelperCat.updateCategory((long) 0,"Categoríaa")){
            Log.d("Tag", "Categoría editada correctamente con id 0");
        }else{
            Log.d("Tag", "Categoría no editada con id 0");
        }
        if(mDbHelperCat.updateCategory((long) -1,"Categoríaa")){
            Log.d("Tag", "Categoría editada correctamente con id -1");
        }else{
            Log.d("Tag", "Categoría no editada con id -1");
        }
        try{
            if(mDbHelperCat.updateCategory((long) n,null)){
                Log.d("Tag", "Categoría editada correctamente con id "+n + " título nulo");
            }
        }catch(Exception e){
            Log.d("Tag", "Categoría no editada con id "+n + " título nulo");
        }
        if(mDbHelperCat.updateCategory((long) n,"")){
            Log.d("Tag", "Categoría editada correctamente con id "+n + " título vacío");
        }else{
            Log.d("Tag", "Categoría no editada con id "+n + " título vacío");
        }
        mDbHelperCat.resetCategories();

    }

}
