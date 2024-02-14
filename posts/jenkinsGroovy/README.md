## Introducción

Ah, el viaje de crear canalizaciones en Jenkins, una historia que comienza tan inocente y pura como un cachorro recién nacido. Empiezas con un `Jenkinsfile` tan sencillo y adorable que casi quieres imprimirlo y colgarlo en la nevera. "Solo un par de etapas", te dices a ti mismo, bañado en la luz de la pantalla, creyendo en un mundo donde todo es simple y comprensible. Pero como con cualquier cachorro, las cosas se complican rápidamente.

Antes de que te des cuenta, tu adorable `Jenkinsfile` se ha multiplicado. Otros equipos, encantados por la simplicidad de tu creación, comienzan a copiar y pegar tu código sagrado como si fuera la receta de la Coca-Cola, creyendo ingenuamente que lo que funciona para uno, por supuesto, funcionará para todos. Es una época de inocencia, donde la esperanza aún brilla... hasta que no lo hace.

De repente, te encuentras en el oscuro mundo de los requisitos que cambian más rápido que los memes en internet, y tu organización se convierte en una pozo de código duplicado, donde cada capa revela una era de decisiones de diseño cada vez más cuestionables. Bienvenidos al caos, donde la promesa de "Pipeline as Code" se siente más como "Chaos as Code".

Pero no todo está perdido en este paisaje desolado de desesperación de desarrollo. Aquí viene Jenkins, con su capa de superhéroe, ofreciendo la salvación a través de las bibliotecas compartidas. Imagina una utopía donde puedes escribir tu lógica una vez y compartirla entre múltiples canalizaciones, un lugar donde el código reutilizable no es solo un mito urbano, sino una realidad palpable. Estas bibliotecas compartidas son como las leyendas de antiguas civilizaciones, comparables a los **JAR** en el mundo **JVM** o los paquetes **Go**, prometiendo un futuro donde puedes, de hecho, tener código bonito y sencillo.

Ah, pero aquí viene la trampa. La documentación de Jenkins sobre cómo manejar estas míticas bibliotecas compartidas es tan detallada como un manual de instrucciones escrito en jeroglíficos. Por suerte para ti, este humilde servidor se ha aventurado en las profundidades de la documentación y ha emergido con conocimientos que se supone que son secretos antiguos. Este blog se dedicará a compartir estas prácticas arcanas, muchas de las cuales, te sorprenderá saber, son aplicables no solo en el reino encantado de Jenkins sino en todo el vasto dominio del desarrollo de software. Prepara tu  **Báculo de debug**, porque vamos a hacer un poco de magia de software.

---

## Diseñando Bibliotecas Compartidas

### Scritps Globales vs Implementaciones de Clases

En el emocionante reino de Jenkins, donde el drama del diseño de bibliotecas compartidas se desarrolla como un episodio de "Juego de Códigos", enfrentamos dos héroes:**Scritps globales**, esos trotamundos del código que pueblan tus código como turistas en temporada alta, y las nobles implementaciones de clases, los caballeros de la estructura y el orden, armados con la magia de las buenas practicas de programación y principios SOLID.

Elegir entre estos dos es como decidir entre ir a una fiesta en pijama o a una gala de etiqueta; mientras que los **Scritps globales** se cuelan en todas partes sin invitación, creando una mucha confusión, las clases mantienen todo en línea, asegurando que cada pieza de lógica tenga su lugar en el código.


### Pipelines Declarativos vs Scripting

Pero aquí no termina la fiesta. La decisión entre una sinfonía declarativa y un jazz de scripts marca el ritmo de nuestro desarrollo. Optar por lo **declarativo** es como seguir una receta de cocina al pie de la letra, prometiendo un futuro lleno de actualizaciones y soporte. En cambio, el **Las pipeline con Scripting** es ese arte culinario libre donde a veces terminas con un platillo estrella o una cocina en llamas.
En este festín de desarrollo, la clave no solo está en elegir sabiamente entre **Clases y Scripts y variables globales**, o entre **Pipelines Declarativos y Scripting**, sino en cómo narras esta epopeya en tu documentación. Porque, al final del día, lo que realmente importa es dejar un legado que haga que las generaciones futuras de desarrolladores levanten sus copas en tu honor, y no que maldigan tu nombre mientras desentrañan el código que forjaste en la forja de Jenkins.