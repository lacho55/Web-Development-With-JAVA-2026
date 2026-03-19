# Theatre Ticketing System - Spring Boot Integration

## Task 1 - migrate your Theatre Ticketing System to a Spring Boot application
1. To achieve this task you will need to create a Spring Boot application with IntelliJ Ultimate or [spring-initializr](https://start.spring.io/) to create a Spring Boot application.

<big><pre>
If you are having trouble with the starter you can check the following [documentation](https://github.com/lacho55/Web-Development-With-JAVA-2026/tree/main/week04/spring-initializr-tutorial.md)
</pre></big>

<big><pre>
In case you need additional information about Spring/Spring Boot/Maven, use the following [documentation](https://github.com/lacho55/Web-Development-With-JAVA-2026/tree/main/week04/basic-guide.md)
</pre></big>


2. Create the proper Bean definition and connect all project dependencies
<details>
<summary>Hints</summary>

```md
- make use of @Autowired and @Component annotation
- Connect the respectful classes (e.g Service is containing Repository, Service can contain one or more Services)
```

</details>

If you don't have the code for the TheatreTicketingSystem project you can use the code from [week03](https://github.com/lacho55/Web-Development-With-JAVA-2026/tree/main/week03-architecture-v1).
For the purpose of this lab you can start only with the Show domain logic (model, repository and service)

## Task 2 - showcase the usage of the beans together with CommandLineRunner interface
For this test we will use the ShowService and ShowController to showcase the autowire and the usage of beans (and as its functionalities are simple to manipulate).

In your main class implement the `CommandLineRunner` interface. This will give you the ability to play with your Spring Boot application

1. Add Shows
2. Display all shows.
3. Update Show with ID 1
4. Display all shows filtered by genre
Example:
```java
@Override
    public void run(String... args) throws Exception {

        System.out.println("🚀 Application started successfully!");

        // Add Shows
        showService.addShow("Hamlet", "Drama", 180);
        showService.addShow("The Nutcracker", "Ballet", 120);

        try {
            showService.addShow("", "Comedy", 90);
        } catch (IllegalArgumentException ex) {
            System.out.println(ex);
        }

        System.out.println("---------------------------------------");
        System.out.println("✅ Shows added successfully!");
        System.out.println("---------------------------------------");

        // Display All Shows
        System.out.println("📌 Displaying all shows:");
        showController.displayAllShows();
        System.out.println("---------------------------------------");

        System.out.println("🔄 Updating 'Hamlet' duration to 200 minutes...");
        showController.updateShow(1, "Hamlet", "Drama", 200);

        System.out.println("---------------------------------------");
        System.out.println("📌 Displaying updated shows:");
        showController.displayAllShows();

        System.out.println("---------------------------------------");
        System.out.println("📌 Displaying all shows by genre 'Drama':");
        List<Show> dramaShows = showController.getShowsByGenre("Drama");
        dramaShows.stream().forEach(System.out::println);
        System.out.println("---------------------------------------");
    }

```
5. Use ApplicationContext to print all loaded beans

<details>
<summary>Hints</summary>

```java
@SpringBootApplication
public class TheatreTicketingSystemApplication implements CommandLineRunner {
    @Autowired
    private ShowController showController;

    @Autowired
    private ShowService showService;

    @Autowired
    private ApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(TheatreTicketingSystemApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println("🚀 Application started successfully!");

        // Add Shows
        showService.addShow("Hamlet", "Drama", 180);
        showService.addShow("The Nutcracker", "Ballet", 120);

        try {
            showService.addShow("", "Comedy", 90);
        } catch (IllegalArgumentException ex) {
            System.out.println(ex);
        }

        System.out.println("---------------------------------------");
        System.out.println("✅ Shows added successfully!");
        System.out.println("---------------------------------------");

        // Display All Shows
        System.out.println("📌 Displaying all shows:");
        showController.displayAllShows();
        System.out.println("---------------------------------------");

        System.out.println("🔄 Updating 'Hamlet' duration to 200 minutes...");
        showController.updateShow(1, "Hamlet", "Drama", 200);

        System.out.println("---------------------------------------");
        System.out.println("📌 Displaying updated shows:");
        showController.displayAllShows();

        System.out.println("---------------------------------------");
        System.out.println("📌 Displaying all shows by genre 'Drama':");
        List<Show> dramaShows = showController.getShowsByGenre("Drama");
        dramaShows.stream().forEach(System.out::println);
        System.out.println("---------------------------------------");

        System.out.println("List of Beans provided by Spring Boot:");

        String[] beanNames = context.getBeanDefinitionNames();
        List<String> beanClasses = Stream.of(beanNames)
            .map(el -> context.getBean(el).getClass().toString())
            .filter(el -> el.contains("bg.uni.fmi"))
            .toList();
        beanClasses.forEach(System.out::println);

        // uncomment to see all loaded beans
//        Arrays.sort(beanNames);
//        for (String beanName : beanNames) {
//            System.out.println(beanName + " - " + context.getBean(beanName).getClass());
//        }
    }
}

```

</details>


## Task 3 - add lombok dependency and remove System print logs if any
1. Search for lombok inside [mvn-repository](https://mvnrepository.com/)
2. Copy the `dependency`
3. Add it inside `pom.xml`
4. Create version property instead of the hardcoded one (e.g 1.18.32)

<details>
<summary>Hints</summary>

```md
- [lombok-repo](https://mvnrepository.com/artifact/org.projectlombok/lombok)
- modify `properties` in order to introduce `lombok.version`
```

</details>

## Task 4 - Ensure that you have basic CRUD operations written for all services
Implement all Services (ShowService, HallService, PerformanceService) connecting them to Repositories using the Spring approach
