# Proyecto GPS

El modulo permite agregarse a diferentes aplicaciones que requieran obtener coordenadas geográficas por medio del chip GPS o GoogleApiClient. Las coordenadas son persistidas en base de datos y pueden ser consultadas por la aplicación. 

## Agregar Modulo a la APP

El modulo se agrega a la aplicación como librería AAR de la siguiente manera:

```groovy
$ cd dillinger
$ npm install -d
$ node app
```
### Configurar Proyecto

Para utilizar el modulo es necesario que la aplicación tenga las siguientes características:

#### Manifest

Permisos que se requieren para obtener coordenadas y persistirlas:

```xml
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

#### Activity

Tal como se puede observar en la aplicación ejemplo, la actividad la cual necesita utilizar la librería debe realizar lo siguiente:

* Implementar el contrato de la vista

```java
public class ExampleGps extends AppCompatActivity implements  GpsContract.View

Al implementar el contrato es necesario declarar los siguientes métodos en la Activity (Estos métodos son la información de salida del módulo (Modulo - App)):
```java
        void goToNext(); 
        void refreshTrackingAdapter(List<Gps> list); // Se refresca la lista con todas las coordenadas almacenadas
        void getListTracking(List<Gps> list); // Se obtiene la lista con todas las coordenadas almacenadas
        void succesStartrackingGps(Boolean isStart); // get true o false, éxito de la escucha o listener de Ubicaciones
        void succesStoprackingGps(Boolean isStop); // get true o false, éxito en detener la escucha o listener de Ubicaciones
```
* Declarar y crear el presenter necesario (Con LoadGpsRepository se obtienen coordenadas con el chip GPS, y con LoadGpsGoogleRepository con Google)

```java
    private GpsContract.Presenter ubicacionPresenter;
    private GpsContract.Presenter ubicacionGooglePresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ubicacionPresenter = new GpsPresenter(this,
                new LoadGps(new LoadGpsRepository(getApplicationContext())));
        ubicacionGooglePresenter =  new GpsPresenter(this,
                new LoadGps(new LoadGpsGoogleRepository(getApplicationContext())))
    }
```
* Iniciar eventos desde la app al módulo (Estos métodos son la información de entrada del módulo (App - Modulo))

```java
  ubicacionPresenter.getListTracking(); // pide al módulo actualizar la lista de coordenadas almacenadas
  ubicacionPresenter.onOrOffGps(true); // pide al módulo encender(true) o apagar(false) el acceso a la ubicacion 
  ubicacionPresenter.starTrackingGps();// iniciar listener para obtener coordenadas
  ubicacionPresenter.stopTrackingGps(); // parar listener para obtener coordenadas
  ubicacionPresenter.intervalTimeGps(periodTrack); // intervalo en (int minutos) para obtener una coordenada
  ubicacionPresenter.refreshTracking(); // refrescar lista de coordenadas almacenadas
```

