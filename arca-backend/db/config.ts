import { defineDb, defineTable, column } from 'astro:db';

const Users = defineTable({
  columns: {
    id: column.text({ primaryKey: true }),
    nome: column.text(),
    email: column.text({ unique: true }),
    senhaHash: column.text(),
    ativo: column.boolean({ default: true }),
    criadoem: column.date({ default: new Date() }),
  }
})

// https://astro.build/db/config
export default defineDb({
  tables: { Users }
});
