package inheritance;

 class Car extends Vehicle {
       String modelName = "Sedan";

    public void playMusic() {
        System.out.println(modelName + " is playing music...");
    }

    public void start(){
        super.start();
        System.out.println("car engine is starting...");
    }
}

