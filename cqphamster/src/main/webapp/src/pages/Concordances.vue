<template>
  <q-page class="flex flex-center">

    <q-card
      class="my-card text-white bg-white absolute-top"
      style="width:100%;margin:0px;"
      elevated
      square
    >
      <q-card-section>
          <div class="row">
              <div class="col-6">
                <div v-bind:key="queryPositions" class="q-gutter-xs">
                  <q-chip clickable @click="editPosition(position)" v-bind:key="position.id" v-for="position in queryPositions" color="primary" removable @remove="remove(position)" text-color="white">
                    {{position.text}}
                  </q-chip>
                  <q-chip @click="add" color="primary" text-color="white" clickable>
                    +
                  </q-chip>                 
                </div>
              </div>

              <div v-if="configWindowMode === 'NONE'" class="col-6">
                Press the '+' button to add a constraint.
              </div>
              
              <div v-else-if="configWindowMode === 'SELECT_CLASS_NEW'" class="col-6">
                <q-btn-group>
                  <q-btn color="primary" label="New Token" @click="newToken" />
                  <q-btn color="primary" label="New Begin of Section" />
                  <q-btn color="primary" label="New End of Section" />
                  <q-btn color="primary" label="New Continuum" />
                </q-btn-group>
              </div>

              <div v-else-if="configWindowMode === 'TOKEN_CONFIG'" class="col-6">
                <div class="row" v-bind:key="attribute" v-for="attribute in selectedPosition.attributes">
                  <div class="col-3">
                    <q-btn-dropdown color="primary" :label="attribute.key">
                      <q-item clickable v-bind:key="tokenAnnotationCategory" v-for="tokenAnnotationCategory in corpus.perTokenAnnotationCategories" @click="setTokenCategory(tokenAnnotationCategory,attribute,selectedPosition)">
                        <q-item-section>
                          <q-item-label>{{tokenAnnotationCategory}}</q-item-label>
                        </q-item-section>
                      </q-item>
                    </q-btn-dropdown>
                  </div>
                  <div class="col-9">

                    <q-input
                      dense
                      v-model="attribute.value"
                      @input="updatePositionText()"
                      stack-label
                    />
                  </div>
                </div>
                <div class="row" style="margin-top:5px;">
                  <div class="col-8">
                  </div>

                  <div class="col-4" >
                    <q-btn-group>
                      <q-btn color="primary" label="Close" @click="close()" />
                      <q-btn color="primary" label="Add Constraint" @click="addTokenAttribute()" />
                      <q-btn-dropdown color="primary" :label="selectedPosition.linker">
                        <q-item clickable @click="setLinker('|')">
                          <q-item-section>
                            <q-item-label>Concat by OR</q-item-label>
                          </q-item-section>
                        </q-item>
                        <q-item clickable @click="setLinker('&')">
                          <q-item-section>
                            <q-item-label>Concat by AND</q-item-label>
                          </q-item-section>
                        </q-item>

                      </q-btn-dropdown>
                  
                    </q-btn-group>

                  </div>
                </div>   
              </div>

              <div v-else-if="configWindowMode === 'BEGIN_SECTION_CONFIG'" class="col-6">

              </div> 

          </div>
      </q-card-section>

      <q-card-actions class="bg-primary">
        <q-btn flat @click="query">Query</q-btn>
        <q-btn flat @click="reset">Reset</q-btn>
        <q-btn flat>Copy Query (as CQL)</q-btn>
        <q-btn flat>Store Query</q-btn>
        <q-btn flat >Send To Expert Mode</q-btn>
        <q-btn flat>Export CSV</q-btn>
        <q-btn flat>Export JSON</q-btn>
      </q-card-actions>
    </q-card>

    <q-table
      title="Concordances"
      :data="concordanceData"
      :columns="tableStructure"
      row-key="name"
      style="width:100%;height:70%;"
      class="absolute-bottom"
    >
      <template v-slot:header="props">
        <q-tr :props="props">
          <q-th auto-width />
          <q-th
            v-for="col in props.cols"
            :key="col.name"
            :props="props"
          >
            {{ col.label }}
          </q-th>
        </q-tr>
      </template>

      <template v-slot:body="props">
        <q-tr :props="props">
          <q-td auto-width>
            <q-btn size="sm" color="accent" round dense @click="props.expand = !props.expand" :icon="props.expand ? 'remove' : 'add'" />
          </q-td>
          <q-td
            v-for="col in props.cols"
            :key="col.name"
            :props="props"
          >
            {{ col.tokens }}
          </q-td>
        </q-tr>
        <q-tr v-show="props.expand" :props="props">
          <q-td colspan="100%">
            <div class="text-left">This is expand slot for row above: {{ props.row.name }}.</div>
          </q-td>
        </q-tr>
      </template>
  
    </q-table>
  </q-page>
</template>

<style>
  .h-cl {
    text-shadow:0px 0px 5px white;
    text-align:center;
    width:100%;
    margin-left:auto;
    margin-right:auto;
  }

</style>

<script>


export default {
  name: "Concordances",

  props: {
    id: Number
  },
  
  methods: {
    query() {
      console.log('submitting');
      let query = '';
      this.queryPositions.forEach(pos => {
        if (pos.type === 'TOKEN_LEVEL') {
          query += '['
          for (let j = 0; j < pos.attributes.length; j++) {
            query += pos.attributes[j].key;
            query += '="';
            query += pos.attributes[j].value;
            query += '"';

            if (j < pos.attributes.length -1) {
              query += pos.linker;
            }
          }
          query += ']';
        }
      })

      console.log(query);

      let m_this = this;

      this.$axios.get('/internal/corpora/' + this.$props.id + '/query/' + encodeURI(query)).then(positions => {
        console.log(positions);
        positions.data.forEach(pos => {
          m_this.$axios.get('/internal/corpora/' + m_this.$props.id + '/kwic/?leftContext='+m_this.leftContext+'&rightContext='+m_this.rightContext+'&from='+pos[0]+'&to='+pos[1]).then(conc => {
            m_this.concordances.push(conc.data);

            console.log(conc.data);
          }).catch(err => {
            console.error(err);
          })
        });
      }).catch(err => {
        console.error(err);
      });
    },


    toggle(mode, id) {
      this.configWindowMode = mode;
      this.configWindowId = id;
    },

    add() {
      this.configWindowMode = 'SELECT_CLASS_NEW',
      this.configWindowId = -1;
    },

    newToken() {
      let id = this.queryPositions.length;
      let newPos = {
        id: id,
        text: '',
        type: 'TOKEN_LEVEL',
        linker: '|',
        attributes: [
          {
            key: 'word',
            value: ''
          }
        ]
      };

      
      this.queryPositions.push(newPos);
      this.selectedPosition = this.queryPositions[this.queryPositions.length -1];
      this.configWindowMode = 'TOKEN_CONFIG';
      this.updatePositionText();
    },

    reset() {
      this.configWindowMode = 'NONE';
      this.configWindowId = -1;
      this.queryPositions = []
    },

    setLinker(link) {
      this.selectedPosition.linker = link;

      this.updatePositionText();
    },

    updatePositionText() {
      let position = this.selectedPosition;

      position.text = ''
      for (let i = 0; i < position.attributes.length; i++) {
        position.text += position.attributes[i].key;
        position.text += ' = ';
        position.text += position.attributes[i].value;
        
        if (i < position.attributes.length -1) {
          position.text += ' ';
          position.text += position.linker;
          position.text += ' ';
        }
      }
    },

    editPosition(position) {
      this.selectedPosition = position;

      if (this.selectedPosition.type === 'TOKEN_LEVEL')
        this.configWindowMode = 'TOKEN_CONFIG';
    },

    setTokenCategory(tokenCategory,attribute,position) {
      attribute.key = tokenCategory;

      this.updatePositionText(position);
    },

    addTokenAttribute() {
      let newAtt = {
        key: 'word',
        value: ''
      }

      this.selectedPosition.attributes.push(newAtt);
      this.updatePositionText();
    },

    remove(position) {
      if (!this.selectedPosition || position.id === this.selectedPosition.id)
        close();

      this.queryPositions.splice(position.id, 1);

      for (let pos in this.queryPositions) {
        if (pos.id > position.id) {
          pos.id -= 1;
        }
      }
    },

    close() {
      this.configWindowMode = 'NONE'
    },



  },

  beforeMount() {
    this.$axios.get('/internal/corpora/' + this.$props.id + '/metadata').then(corpus => {
      this.corpus = corpus.data;
    }).catch(err => {
      console.error(err);
    });
  },

  data () {
    return {
      tableStructure: [
        {
          name: 'showAnnotations',
          label: 'A'
        },
        {
          name: 'leftContext',
          label: 'Left Context'
        },
        {
          name: 'conc',
          label: 'Keyword/-phrase'
        },
        {
          name: 'rightContext',
          label: 'Right Context'
        }
      ],

      leftContext: 5,
      rightContext: 5,

      concordances: [],

      corpus: {
        id: 0,
        perTokenAnnotationCategories: ['token','pos','lemma']
      },

      configWindowMode: 'NONE',
      configWindowId: -1,
      selectedPosition: undefined,
      queryPositions: [
        {
          id: 0,
          text: 'word = hello',
          type: 'TOKEN_LEVEL',
          linker: '|',
          attributes: [
            {
              key: 'word',
              value: 'hello'
            }
          ]
        },
        {
          id: 1,
          text: 'word = world',
          type: 'TOKEN_LEVEL',
          linker: '|',
          attributes: [
            {
              key: 'word',
              value: 'world'
            }
          ]
        }    
      ]
    }
  }
};
</script>