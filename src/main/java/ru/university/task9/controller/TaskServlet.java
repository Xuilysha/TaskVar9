package ru.university.task9.controller;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import ru.university.task9.model.CarData;
import ru.university.task9.model.Scenario;
import ru.university.task9.service.SimService;

public class TaskServlet extends HttpServlet {
    private SimService service;

    public TaskServlet(SimService service) {
        this.service = service;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            String path = req.getPathInfo();
            if (path == null || path.equals("/")) {
                home(res);
            } else if (path.equals("/scenario")) {
                scenario(res, num(req, "id"));
            } else if (path.equals("/run")) {
                run(res, num(req, "id"), num(req, "steps"));
            } else {
                error(res, 404, "страница не найдена");
            }
        } catch (IllegalArgumentException e) {
            error(res, 400, e.getMessage());
        } catch (SQLException e) {
            error(res, 500, "ошибка базы данных: " + e.getMessage());
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            String path = req.getPathInfo();
            if ("/new".equals(path)) {
                String kind = req.getParameter("kind");
                int stopRoad = "round".equals(kind) ? -1 : num(req, "stop") - 1;
                int id = service.addScenario(req.getParameter("name"), req.getParameter("kind"),
                        num(req, "roads"), num(req, "lanes"), num(req, "size"), stopRoad);
                res.sendRedirect("/scenario?id=" + id);
            } else if ("/car".equals(path)) {
                int id = num(req, "id");
                String turn = req.getParameter("turn");
                int to = req.getParameter("to") == null ? 0 : num(req, "to") - 1;
                CarData a = new CarData(0, id, req.getParameter("tip"), num(req, "len"),
                        num(req, "lane") - 1, num(req, "pos"), num(req, "from") - 1, to, turn,
                        num(req, "changeStep"), num(req, "changeLane") - 1);
                service.addCar(a);
                res.sendRedirect("/scenario?id=" + id);
            } else if ("/delete".equals(path)) {
                service.deleteScenario(num(req, "id"));
                res.sendRedirect("/");
            } else if ("/delete-car".equals(path)) {
                int id = num(req, "id");
                service.deleteCar(num(req, "car"), id);
                res.sendRedirect("/scenario?id=" + id);
            } else {
                error(res, 404, "страница не найдена");
            }
        } catch (IllegalArgumentException e) {
            error(res, 400, e.getMessage());
        } catch (SQLException e) {
            error(res, 500, "ошибка базы данных: " + e.getMessage());
        }
    }

    private int num(HttpServletRequest req, String key) {
        try {
            return Integer.parseInt(req.getParameter(key));
        } catch (Exception e) {
            throw new IllegalArgumentException("неверное число: " + key);
        }
    }

    private String esc(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }

    private PrintWriter start(HttpServletResponse res, String title) throws IOException {
        res.setContentType("text/html; charset=UTF-8");
        PrintWriter w = res.getWriter();
        w.println("<!doctype html><html lang='ru'><meta charset='UTF-8'><title>" + esc(title) + "</title>");
        w.println("<style>body{font:16px Arial;max-width:850px;margin:30px auto;padding:0 15px}" +
                "form{margin:15px 0;padding:12px;background:#f1f1f1}label{display:block;margin:8px 0}" +
                "input,select{padding:5px}button{padding:6px 12px}pre{overflow:auto;background:#eee;padding:15px}" +
                "li{margin:8px 0}</style><body><a href='/'>Все сценарии</a><h1>" + esc(title) + "</h1>");
        return w;
    }

    private void home(HttpServletResponse res) throws IOException, SQLException {
        ArrayList<Scenario> list = service.all();
        PrintWriter w = start(res, "Вариант 9 - движение машин");
        w.println("<h2>Сценарии</h2><ul>");
        for (Scenario a : list) {
            w.println("<li><a href='/scenario?id=" + a.id + "'>" + esc(a.name) + "</a> - " +
                    (a.kind.equals("cross") ? "обычный" : "круговой") + " перекресток</li>");
        }
        w.println("</ul><h2>Новый сценарий</h2><form method='post' action='/new'>");
        w.println("<label>Название <input name='name' required maxlength='100'></label>");
        w.println("<label>Вид <select name='kind'><option value='cross'>обычный</option>" +
                "<option value='round'>круговой</option></select></label>");
        w.println("<label>Число дорог (3-4 для обычного, 3-6 для кругового) " +
                "<input type='number' name='roads' value='4' min='3' max='6'></label>");
        w.println("<label>Полосы (1-4) <input type='number' name='lanes' value='2' min='1' max='4'></label>");
        w.println("<label>Клетки на дороге (3-30) <input type='number' name='size' value='5' min='3' max='30'></label>");
        w.println("<label>Знак стоп на въезде (0 - нет, только для обычного) " +
                "<input type='number' name='stop' value='0' min='0' max='4'></label>");
        w.println("<button>Создать</button></form></body></html>");
    }

    private void scenario(HttpServletResponse res, int id) throws IOException, SQLException {
        Scenario a = service.get(id);
        if (a == null) {
            error(res, 404, "сценарий не найден");
            return;
        }
        ArrayList<CarData> cars = service.cars(id);
        PrintWriter w = start(res, a.name);
        w.println("<p>" + (a.kind.equals("cross") ? "Обычный" : "Круговой") + " перекресток. " +
                "Дорог: " + a.roads + ", полос: " + a.lanes + ", клеток: " + a.size + ".</p>");
        if (a.stopRoad >= 0) w.println("<p>Знак стоп на въезде " + (a.stopRoad + 1) + ".</p>");
        w.println("<h2>Машины</h2><ul>");
        for (CarData c : cars) {
            w.println("<li>№" + c.id + " " + esc(c.tip) + ", длина " + c.len + ", дорога " +
                    (c.fromRoad + 1) + ", полоса " + (c.lane + 1) + ", клетка " + c.pos +
                    (a.kind.equals("cross") ? ", " + esc(c.turn) : ", выезд " + (c.toRoad + 1)) +
                    (c.changeStep > 0 ? ", перестроение на шаге " + c.changeStep +
                            " в полосу " + (c.changeLane + 1) : "") +
                    " <form method='post' action='/delete-car' style='display:inline;padding:0;background:none'>" +
                    "<input type='hidden' name='id' value='" + id + "'>" +
                    "<input type='hidden' name='car' value='" + c.id + "'><button>Удалить</button></form></li>");
        }
        w.println("</ul><h2>Добавить машину</h2><form method='post' action='/car'>");
        w.println("<input type='hidden' name='id' value='" + id + "'>");
        w.println("<label>Тип <input name='tip' value='легковая' required maxlength='50'></label>");
        w.println("<label>Длина <input type='number' name='len' value='1' min='1' max='" + a.size + "'></label>");
        w.println("<label>Дорога въезда (с 1) <input type='number' name='from' value='1' min='1' max='" + a.roads + "'></label>");
        w.println("<label>Полоса (с 1) <input type='number' name='lane' value='1' min='1' max='" + a.lanes + "'></label>");
        w.println("<label>Начальная клетка (с 0) <input type='number' name='pos' value='0' min='0' max='" + (a.size - 1) + "'></label>");
        if (a.kind.equals("cross")) {
            w.println("<label>Поворот <select name='turn'><option>прямо</option><option>налево</option>" +
                    "<option>направо</option><option>разворот</option></select></label>");
        } else {
            w.println("<label>Дорога выезда (с 1) <input type='number' name='to' value='2' min='1' max='" + a.roads + "'></label>");
        }
        w.println("<label>Перестроение на шаге (0 - нет) " +
                "<input type='number' name='changeStep' value='0' min='0' max='100'></label>");
        w.println("<label>Новая полоса при перестроении (с 1) " +
                "<input type='number' name='changeLane' value='1' min='1' max='" + a.lanes + "'></label>");
        w.println("<button>Добавить</button></form>");
        w.println("<h2>Запуск</h2><form method='get' action='/run'><input type='hidden' name='id' value='" + id + "'>" +
                "<label>Число шагов (0-100) <input type='number' name='steps' value='10' min='0' max='100'></label>" +
                "<button>Показать результат</button></form>");
        w.println("<form method='post' action='/delete'><input type='hidden' name='id' value='" + id + "'>" +
                "<button>Удалить сценарий</button></form></body></html>");
    }

    private void run(HttpServletResponse res, int id, int steps) throws IOException, SQLException {
        String result = service.run(id, steps);
        PrintWriter w = start(res, "Результат симуляции");
        w.println("<p><a href='/scenario?id=" + id + "'>Назад к сценарию</a></p>");
        w.println("<pre>" + esc(result) + "</pre></body></html>");
    }

    private void error(HttpServletResponse res, int status, String text) throws IOException {
        res.setStatus(status);
        PrintWriter w = start(res, "Ошибка");
        w.println("<p>" + esc(text) + "</p></body></html>");
    }
}
