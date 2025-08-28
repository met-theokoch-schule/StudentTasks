{ pkgs }: {
    deps = [
      pkgs.wkhtmltopdf
      pkgs.wkhtmltopdf-bin
        pkgs.graalvm17-ce
        pkgs.maven
        pkgs.replitPackages.jdt-language-server
        pkgs.replitPackages.java-debug
    ];
}